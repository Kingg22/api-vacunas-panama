package io.github.kingg22.api.vacunas.panama.modules.usuario.service

import io.github.kingg22.api.vacunas.panama.modules.fabricante.entity.toFabricanteDto
import io.github.kingg22.api.vacunas.panama.modules.fabricante.service.FabricanteService
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.toPersonaDto
import io.github.kingg22.api.vacunas.panama.modules.persona.service.PersonaService
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RegisterUserDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RestoreDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RolDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RolesEnum
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.UsuarioDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Usuario
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Usuario.Companion.builder
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.toUsuarioDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.repository.UsuarioRepository
import io.github.kingg22.api.vacunas.panama.modules.usuario.service.RegistrationResult.RegistrationError
import io.github.kingg22.api.vacunas.panama.response.ApiContentResponse
import io.github.kingg22.api.vacunas.panama.response.ApiError
import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createApiErrorBuilder
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createContentResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createResponseBuilder
import io.github.kingg22.api.vacunas.panama.response.returnIfErrors
import io.github.kingg22.api.vacunas.panama.util.FormatterUtil.formatToSearch
import io.github.kingg22.api.vacunas.panama.util.logger
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.context.annotation.Lazy
import org.springframework.data.jpa.repository.Modifying
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.password.ReactiveCompromisedPasswordChecker
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.util.Optional
import java.util.UUID

@Service
class UsuarioServiceImpl(
    private val reactiveCompromisedPasswordChecker: ReactiveCompromisedPasswordChecker,
    private val passwordEncoder: PasswordEncoder,
    private val usuarioRepository: UsuarioRepository,
    private val rolPermisoService: RolPermisoService,
    private val registrationStrategyFactory: RegistrationStrategyFactory,
    @Lazy private val personaService: PersonaService,
    @Lazy private val fabricanteService: FabricanteService,
    @Lazy private val tokenService: TokenService,
) : UsuarioService {
    private val log = logger()

    // TODO remove set of disabled
    @Transactional
    override fun getUsuarioByIdentifier(identifier: String): Optional<Usuario> {
        val formatted = formatToSearch(identifier)

        val usuario = usuarioRepository.findByUsername(identifier)
            .or {
                usuarioRepository.findByCedulaOrPasaporteOrCorreo(
                    formatted.cedula,
                    formatted.pasaporte,
                    formatted.correo,
                )
            }
            .or {
                usuarioRepository.findByLicenciaOrCorreo(identifier, identifier).map { user ->
                    log.debug("Found user: {}, with credentials of Fabricante", user.id)
                    fabricanteService.getFabricanteByUserID(user.id!!).ifPresent { f ->
                        user.disabled = f.disabled
                    }
                    user
                }
            }

        usuario.ifPresent {
            personaService.getPersonaByUserID(it.id!!).ifPresent { p ->
                log.debug("Found user: {}, with credentials of Persona", it.id)
                it.disabled = p.disabled
            }
        }

        return usuario
    }

    override fun getUsuarioById(id: UUID) = usuarioRepository.findById(id)

    override fun getProfile(id: UUID): ApiContentResponse = createResponseBuilder {
        personaService
            .getPersonaByUserID(id)
            .ifPresent { doctor -> withData("persona", doctor.toPersonaDto()) }
        fabricanteService
            .getFabricanteByUserID(id)
            .ifPresent { fabricante -> withData("fabricante", fabricante.toFabricanteDto()) }
    } as ApiContentResponse

    override fun getLogin(id: UUID): ApiContentResponse = createResponseBuilder {
        usuarioRepository.findById(id).ifPresentOrElse({
            if (it.persona != null) {
                withData("persona", it.persona!!.toPersonaDto())
            }
            if (it.fabricante != null) {
                withData("fabricante", it.fabricante!!.toFabricanteDto())
            }
            withData(tokenService.generateTokens(it.toUsuarioDto()))
        }) {
            withError(
                ApiResponseCode.NOT_FOUND,
                "El usuario no ha sido encontrado, intente nuevamente.",
            )
        }
    }

    @Transactional
    override suspend fun createUser(
        registerUserDto: RegisterUserDto,
        authentication: Authentication?,
    ): ApiContentResponse {
        val response = createContentResponse()
        val usuarioDto = registerUserDto.usuario

        authentication?.let {
            if (authentication.isAuthenticated &&
                authentication !is AnonymousAuthenticationToken
            ) {
                response.addErrors(validateAuthoritiesRegister(usuarioDto, it))
                response.returnIfErrors()?.let { return it }
            }
        }

        response.addWarnings(validateWarningsRegistration(usuarioDto))

        val validationResult = validateRegistration(registerUserDto)
        if (validationResult is RegistrationError) {
            response.addErrors(validationResult.errors)
            return response
        }

        val strategy = registrationStrategyFactory.getStrategy(registerUserDto)
            ?: return response.apply {
                addError(
                    createApiErrorBuilder {
                        withCode(ApiResponseCode.API_UPDATE_UNSUPPORTED)
                        withMessage("No se encontró estrategia válida")
                    },
                )
            }

        val finalResponse = strategy.create(registerUserDto)
        response.mergeContentResponse(finalResponse)
        return response
    }

    override fun createUser(usuarioDto: UsuarioDto, block: (Usuario) -> Unit): Usuario {
        val role = usuarioDto.roles
            ?.mapNotNull {
                rolPermisoService.convertToRole(it).also { found ->
                    if (found == null) log.warn("Rol no encontrado: ${it.nombre ?: it.id}")
                }
            }
            ?.toSet() ?: emptySet()

        val usuario = builder()
            .username(usuarioDto.username)
            .password(passwordEncoder.encode(usuarioDto.password))
            .createdAt(usuarioDto.createdAt ?: LocalDateTime.now(UTC))
            .roles(role)
            .build()

        block(usuario)
        return usuarioRepository.save(usuario)
    }

    @Transactional
    override suspend fun changePassword(restoreDto: RestoreDto): ApiContentResponse {
        val response = createResponseBuilder()
        val usuarioOpt = getUsuarioByIdentifier(restoreDto.username)
        if (usuarioOpt.isPresent) {
            val usuario = usuarioOpt.get()
            response.build().addErrors(validateChangePassword(usuario, restoreDto))
            if (!response.build().hasErrors()) {
                usuario.password = passwordEncoder.encode(restoreDto.newPassword)
                usuarioRepository.save(usuario)
            }
        } else {
            response.withError(
                ApiResponseCode.NOT_FOUND,
                "La persona con la identificación dada no fue encontrada",
                "username",
            )
        }
        return response.build()
    }

    @Modifying
    override fun updateLastUsed(id: UUID) {
        usuarioRepository.findById(id).ifPresentOrElse({
            it.lastUsed = LocalDateTime.now(UTC)
            usuarioRepository.save(it)
        }) {
            log.error("Cannot find a user with id {} for update last used", id)
        }
    }

    private suspend fun validateChangePassword(usuario: Usuario, restoreDto: RestoreDto): List<ApiError> {
        val newPassword = "new_password"
        val builder = createResponseBuilder()
        if (passwordEncoder.matches(restoreDto.newPassword, usuario.password)) {
            builder.withError(
                ApiResponseCode.VALIDATION_FAILED,
                "La nueva contraseña no puede ser igual a la contraseña actual",
                newPassword,
            )
        }
        if (usuario.username != null && restoreDto.newPassword.contains(usuario.username!!, true)) {
            builder.withError(
                ApiResponseCode.VALIDATION_FAILED,
                "La nueva contraseña no puede tener su username",
                newPassword,
            )
        }
        if (reactiveCompromisedPasswordChecker.check(restoreDto.newPassword).awaitSingle().isCompromised) {
            builder.withError(
                ApiResponseCode.VALIDATION_FAILED,
                "La nueva contraseña está comprometida, utilice contraseñas seguras",
                newPassword,
            )
        }
        return builder.build().errors
    }

    private fun validateAuthoritiesRegister(usuarioDto: UsuarioDto, authentication: Authentication): List<ApiError> {
        val authenticatedRoles = authentication.authorities
            .mapNotNull { it.authority.removePrefix("ROLE_").takeIf(String::isNotBlank) }
            .mapNotNull { runCatching { RolesEnum.valueOf(it) }.getOrNull() }

        val errors = mutableListOf<ApiError>()

        usuarioDto.roles?.let { requestedRoles ->
            if (!requestedRoles.all { canRegisterRole(it, authenticatedRoles) }) {
                errors += createApiErrorBuilder {
                    withCode(ApiResponseCode.ROL_HIERARCHY_VIOLATION)
                    withProperty("roles[]")
                    withMessage("No puede asignar roles superiores a su rol actual")
                }
            }
        }

        if (!hasUserManagementPermissions(authenticatedRoles.map { it.name })) {
            errors += createApiErrorBuilder {
                withCode(ApiResponseCode.PERMISSION_DENIED)
                withMessage("No tienes permisos para registrar")
            }
        }

        return errors
    }

    private fun validateWarningsRegistration(usuarioDto: UsuarioDto): List<ApiError> {
        val apiErrorList = mutableListOf<ApiError>()
        if (usuarioDto.roles != null &&
            usuarioDto.roles.stream()
                .anyMatch { rolDto: RolDto? -> rolDto!!.permisos != null && rolDto.permisos.isNotEmpty() }
        ) {
            apiErrorList += createApiErrorBuilder {
                withCode(ApiResponseCode.INFORMATION_IGNORED)
                withProperty("roles[].permisos[]")
                withMessage(
                    "Los permisos de los roles son ignorados al crear un usuario. Para crear o relacionar nuevos permisos a un rol debe utilizar otra opción",
                )
            }
        }
        if (usuarioDto.roles != null &&
            usuarioDto.roles.any { rolDto -> rolDto.id == null && rolDto.nombre != null && !rolDto.nombre.isBlank() }
        ) {
            apiErrorList +=
                createApiErrorBuilder {
                    withCode(ApiResponseCode.NON_IDEMPOTENCE)
                    withProperty("roles[]")
                    withMessage("Utilice ID al realizar peticiones")
                }
        }
        return apiErrorList
    }

    private fun canRegisterRole(rolDto: RolDto, authenticatedRoles: List<RolesEnum>): Boolean {
        val maxRolPriority = authenticatedRoles.maxBy { it.priority }.priority
        return rolDto.nombre != null && RolesEnum.valueOf(rolDto.nombre.uppercase()).priority <= maxRolPriority
    }

    private fun hasUserManagementPermissions(authenticatedAuthorities: List<String>) =
        authenticatedAuthorities.contains("ADMINISTRATIVO_WRITE") ||
            authenticatedAuthorities.contains("AUTORIDAD_WRITE") ||
            authenticatedAuthorities.contains("USER_MANAGER_WRITE")

    private suspend fun validateRegistration(registerUserDto: RegisterUserDto): RegistrationResult {
        val errors = mutableListOf<ApiError>()
        val usuarioDto = registerUserDto.usuario

        if (isUsernameRegistered(usuarioDto.username)) {
            errors += createApiErrorBuilder {
                withCode(ApiResponseCode.ALREADY_TAKEN)
                withProperty("username")
                withMessage("El nombre de usuario ya está en uso")
            }
        }

        if (reactiveCompromisedPasswordChecker.check(usuarioDto.password).awaitSingle().isCompromised) {
            errors += createApiErrorBuilder {
                withCode(ApiResponseCode.COMPROMISED_PASSWORD)
                withProperty("password")
                withMessage("La contraseña proporcionada está comprometida. Por favor use otra contraseña")
            }
        }

        return if (errors.isEmpty()) RegistrationResult.RegistrationSuccess(Any()) else RegistrationError(errors)
    }

    fun isUsernameRegistered(username: String?) =
        username != null && usuarioRepository.findByUsername(username).isPresent
}
