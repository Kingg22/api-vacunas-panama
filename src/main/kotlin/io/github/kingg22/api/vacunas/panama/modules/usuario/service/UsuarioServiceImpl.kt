package io.github.kingg22.api.vacunas.panama.modules.usuario.service

import io.github.kingg22.api.vacunas.panama.modules.fabricante.entity.Fabricante
import io.github.kingg22.api.vacunas.panama.modules.fabricante.entity.toFabricanteDto
import io.github.kingg22.api.vacunas.panama.modules.fabricante.service.FabricanteService
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.toPersonaDto
import io.github.kingg22.api.vacunas.panama.modules.persona.service.PersonaService
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RegisterUserDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RestoreDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RolDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RolesEnum
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.UsuarioDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.toRol
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.toUsuario
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Usuario
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.toUsuarioDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.persistence.UsuarioPersistenceService
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
import jakarta.persistence.EntityManager
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.context.annotation.Lazy
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.password.ReactiveCompromisedPasswordChecker
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.util.UUID

@Service
class UsuarioServiceImpl(
    private val entityManager: EntityManager,
    private val transactionTemplate: TransactionTemplate,
    private val reactiveCompromisedPasswordChecker: ReactiveCompromisedPasswordChecker,
    private val passwordEncoder: PasswordEncoder,
    private val usuarioPersistenceService: UsuarioPersistenceService,
    @Lazy private val registrationStrategyFactory: RegistrationStrategyFactory,
    @Lazy private val rolPermisoService: RolPermisoService,
    @Lazy private val personaService: PersonaService,
    @Lazy private val fabricanteService: FabricanteService,
    @Lazy private val tokenService: TokenService,
) : UsuarioService {
    private val log = logger()

    override suspend fun getUsuarioByIdentifier(identifier: String): UsuarioDto? {
        val byUsername = usuarioPersistenceService.findByUsername(identifier)?.toUsuarioDto()?.also {
            log.debug("Found user by username: {}", it.id)
        }
        if (byUsername != null) return byUsername

        val formatted = formatToSearch(identifier)
        val byPersona = usuarioPersistenceService.findByCedulaOrPasaporteOrCorreo(
            formatted.cedula,
            formatted.pasaporte,
            formatted.correo,
        )?.toUsuarioDto()?.also {
            log.debug("Found user: {}, with credentials of Persona", it.id)
        }
        if (byPersona != null) return byPersona

        return usuarioPersistenceService.findByLicenciaOrCorreo(identifier, identifier)?.toUsuarioDto()?.also {
            log.debug("Found user: {}, with credentials of Fabricante", it.id)
        }
    }

    override suspend fun getUsuarioById(id: UUID) = usuarioPersistenceService.findUsuarioById(id)?.toUsuarioDto()

    override suspend fun getProfile(id: UUID): ApiContentResponse {
        val builder = createResponseBuilder()
        personaService.getPersonaByUserID(id)?.let { persona -> builder.withData("persona", persona) }
        fabricanteService.getFabricanteByUserID(id)?.let { fabricante -> builder.withData("fabricante", fabricante) }
        return builder.build()
    }

    override suspend fun getLogin(id: UUID): ApiContentResponse {
        val response = createResponseBuilder()
        val usuario = usuarioPersistenceService.findUsuarioById(id)
        if (usuario != null) {
            if (usuario.persona != null) {
                response.withData("persona", usuario.persona!!.toPersonaDto())
            }
            if (usuario.fabricante != null) {
                response.withData("fabricante", usuario.fabricante!!.toFabricanteDto())
            }
            response.withData(tokenService.generateTokens(usuario.toUsuarioDto()))
        } else {
            response.withError(
                ApiResponseCode.NOT_FOUND,
                "El usuario no ha sido encontrado, intente nuevamente.",
            )
        }
        return response.build()
    }

    override suspend fun createUser(
        registerUserDto: RegisterUserDto,
        authentication: Authentication?,
    ): ApiContentResponse {
        val response = createContentResponse()
        val usuarioDto = registerUserDto.usuario

        authentication?.let { authentication ->
            if (authentication.isAuthenticated &&
                authentication !is AnonymousAuthenticationToken
            ) {
                response.addErrors(validateAuthoritiesRegister(usuarioDto, authentication))
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
                        withMessage("No se encontró estrategia válida para registrarse")
                    },
                )
            }

        val finalResponse = strategy.create(registerUserDto)
        response.mergeContentResponse(finalResponse)
        return response
    }

    override suspend fun createUser(usuarioDto: UsuarioDto, persona: Persona?, fabricante: Fabricante?) {
        val roles = rolPermisoService.convertToExistRol(usuarioDto.roles).map { it.toRol() }.toMutableSet()
        transactionTemplate.execute {
            val managedPersona = persona?.let { entityManager.merge(it) }
            val managedFabricante = fabricante?.let { entityManager.merge(it) }

            val usuario = Usuario(
                username = usuarioDto.username,
                clave = passwordEncoder.encode(usuarioDto.password),
                createdAt = usuarioDto.createdAt,
                roles = roles,
                persona = persona,
                fabricante = fabricante,
                id = usuarioDto.id,
                disabled = usuarioDto.disabled,
            )
            managedPersona?.usuario = usuario
            managedFabricante?.usuario = usuario

            // Using entityManager directly, since we're in a transaction and this, it'd not suspend the function body
            entityManager.persist(usuario)
            log.trace("User created: {}", usuario)
        }
    }

    override suspend fun changePassword(restoreDto: RestoreDto): ApiContentResponse {
        val response = createResponseBuilder()
        val usuarioOpt = getUsuarioByIdentifier(restoreDto.username)

        if (usuarioOpt == null) {
            response.withError(
                ApiResponseCode.NOT_FOUND,
                "La persona con la identificación dada no fue encontrada",
                "username",
            )
            return response.build()
        }

        val usuario = usuarioOpt
        response.withError(validateChangePassword(usuario, restoreDto))

        personaService.getPersonaByUserID(usuario.id!!)?.let {
            if (it.fechaNacimiento!!.toLocalDate() != restoreDto.fechaNacimiento) {
                response.withError(
                    ApiResponseCode.VALIDATION_FAILED,
                    "La fecha de nacimiento no coincide con la registrada",
                    "fecha_nacimiento",
                )
            }
        } ?: response.withError(
            ApiResponseCode.VALIDATION_FAILED,
            "No se pudo encontrar la persona asociada al usuario",
        )

        if (!response.hasErrors()) {
            val persistenceUsuario = usuario.copy(password = passwordEncoder.encode(restoreDto.newPassword))
                .toUsuario()
            usuarioPersistenceService.saveUsuario(persistenceUsuario)
        }

        return response.build()
    }

    override suspend fun updateLastUsed(id: UUID) {
        val usuario = usuarioPersistenceService.findUsuarioById(id)
        if (usuario == null) {
            log.error("Cannot find a user with id {} for update last used", id)
        } else {
            usuario.lastUsed = LocalDateTime.now(UTC)
            usuarioPersistenceService.saveUsuario(usuario)
        }
    }

    private suspend fun validateChangePassword(usuario: UsuarioDto, restoreDto: RestoreDto): List<ApiError> {
        val newPassword = "new_password"
        val builder = createResponseBuilder()
        if (passwordEncoder.matches(restoreDto.newPassword, usuario.password)) {
            builder.withError(
                ApiResponseCode.VALIDATION_FAILED,
                "La nueva contraseña no puede ser igual a la contraseña actual",
                newPassword,
            )
        }
        if (usuario.username != null && restoreDto.newPassword.contains(usuario.username, true)) {
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

        if (!usuarioDto.roles.all { canRegisterRole(it, authenticatedRoles) }) {
            errors += createApiErrorBuilder {
                withCode(ApiResponseCode.ROL_HIERARCHY_VIOLATION)
                withProperty("roles[]")
                withMessage("No puede asignar roles superiores a su rol actual")
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
        if (usuarioDto.roles.any { rolDto -> rolDto.permisos.isNotEmpty() }
        ) {
            apiErrorList += createApiErrorBuilder {
                withCode(ApiResponseCode.INFORMATION_IGNORED)
                withProperty("roles[].permisos[]")
                withMessage(
                    "Los permisos de los roles son ignorados al crear un usuario. Para crear o relacionar nuevos permisos a un rol debe utilizar otra opción",
                )
            }
        }
        if (usuarioDto.roles.any { rolDto -> rolDto.id == null && rolDto.nombre != null && !rolDto.nombre.isBlank() }) {
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

    suspend fun isUsernameRegistered(username: String?) =
        username != null && usuarioPersistenceService.findByUsername(username) != null
}
