package io.github.kingg22.api.vacunas.panama.modules.usuario.controller

import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.LoginDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RegisterUserDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RestoreDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.service.UsuarioService
import io.github.kingg22.api.vacunas.panama.response.ActualApiResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createApiErrorBuilder
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createResponseEntity
import io.github.kingg22.api.vacunas.panama.util.HaveIBeenPwnedPasswordChecker
import io.github.kingg22.api.vacunas.panama.util.bcryptMatch
import io.github.kingg22.api.vacunas.panama.util.isCompromisedUsing
import io.github.kingg22.api.vacunas.panama.util.logger
import io.vertx.ext.web.RoutingContext
import jakarta.annotation.security.PermitAll
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.PATCH
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.jwt.JsonWebToken
import java.util.UUID

/**
 * Controller for `Usuario` registration and management, `Rol` and `Permiso`.
 *
 * This controller handles operations related to registering users and managing their roles and associated entities
 * (e.g., `Paciente`, `Doctor`, `Fabricante`). It ensures that users are linked to an existing
 * `Persona` or `Entidad` and properly assigned roles.
 *
 * **Response Format**: The response for registration and related endpoints typically includes:
 *
 *  * User details (e.g., username, roles, etc.).
 *  * Associated `Persona` or `Entidad` information (e.g., `Paciente`, `Doctor`, `Fabricante`) if applicable.
 *  * A JWT token, which is only generated if the associated persona or entity has an active (validated) status.
 *
 * @see io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Usuario
 * @see io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Rol
 * @see io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Permiso
 * @see io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona
 * @see io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente
 * @see io.github.kingg22.api.vacunas.panama.modules.doctor.entity.Doctor
 * @see io.github.kingg22.api.vacunas.panama.modules.common.entity.Entidad
 * @see io.github.kingg22.api.vacunas.panama.modules.fabricante.entity.Fabricante
 */
@Path("/account")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
class UsuarioController(
    private val pwChecker: HaveIBeenPwnedPasswordChecker,
    private val usuarioService: UsuarioService,
) {
    private val log = logger()

    @Inject
    lateinit var jwt: JsonWebToken

    /**
     * Handles user registration.
     *
     * First, it checks if the current [Authentication] is for an authenticated user with sufficient
     * permissions to create users with lower `Rol`. If not authenticated, it allows registering a
     * `Paciente`. It also validates the data to be registered (e.g., username, email) is not currently in use.
     *
     * If all validations pass, the `Usuario` is created.
     *
     * **Note**: The user must be assigned roles, and empty roles are not allowed. If the associated entities are
     * not created, the request will be rejected. For cases where both the `Persona` / `Entidad` and the
     * `Usuario` need to create in a single request, a different endpoint should be used.
     *
     * @param registerUserDto The [RegisterUserDto] containing the user registration details.
     * @param authentication The [Authentication] representing the current user (if any).
     * @param request The [ServerHttpRequest] used for building the response.
     * @return [ActualApiResponse] containing the registration result, including user details, associated
     * `Persona` or `Entidad` information and a token if the `Persona` or `Entidad` is
     * validated and active.
     *
     * @see io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Usuario
     * @see io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Rol
     * @see io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona
     * @see io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente
     * @see io.github.kingg22.api.vacunas.panama.modules.common.entity.Entidad
     */
    @Path("/register")
    @POST
    @PermitAll
    suspend fun register(@Valid registerUserDto: RegisterUserDto, rc: RoutingContext): Response {
        val apiResponse = createResponse()
        val usuarioDto = registerUserDto.usuario

        /*
                if (authentication == null &&
                    !usuarioDto.roles.all { rolDto: RolDto ->
                        rolDto.id != null &&
                            getByPriority(rolDto.id) == RolesEnum.PACIENTE ||
                            rolDto.nombre != null &&
                            rolDto.nombre.equals(RolesEnum.PACIENTE.name, ignoreCase = true)
                    }
                ) {
                    apiResponse.addError(
                        createApiErrorBuilder {
                            withCode(ApiResponseCode.MISSING_ROLE_OR_PERMISSION)
                            withMessage("Solo pacientes pueden registrarse sin autenticación")
                        },
                    )
                    apiResponse.addStatusCode(403)
                    apiResponse.addStatus("message", ApiResponseCode.INSUFFICIENT_ROLE_PRIVILEGES)
                    return createResponseEntity(apiResponse, request)
                }
         */

        apiResponse.mergeContentResponse(usuarioService.createUser(registerUserDto, null))
        if (apiResponse.hasErrors()) {
            apiResponse.addStatusCode(400)
        } else {
            apiResponse.addStatusCode(201)
        }
        return createResponseEntity(apiResponse, rc)
    }

    @Path("/login")
    @POST
    @PermitAll
    suspend fun login(@Valid loginDto: LoginDto, rc: RoutingContext): Response {
        val apiResponse = createResponse()
        val usuarioDto = usuarioService.getUsuarioByIdentifier(loginDto.username)
        if (usuarioDto == null) {
            apiResponse.addStatusCode(401)
            apiResponse.addStatus("message", "Intente nuevamente")
            return createResponseEntity(apiResponse, rc)
        }
        checkNotNull(usuarioDto.id) { "Usuario id is null after retrieve with identifier: ${loginDto.username}" }
        if (loginDto.password.isCompromisedUsing(pwChecker)) {
            log.debug("CompromisedPassword for user with identifier: {}", loginDto.username)
            apiResponse.addStatusCode(307)
            apiResponse.addStatus("Please reset your password in the given uri", "/vacunacion/v1/account/restore")
            apiResponse.addError(
                createApiErrorBuilder {
                    withCode(ApiResponseCode.COMPROMISED_PASSWORD)
                    withProperty("password")
                    withMessage("Su contraseña está comprometida, por favor cambiarla lo más pronto posible")
                },
            )
            return createResponseEntity(apiResponse, rc)
        }
        if (loginDto.password.bcryptMatch(usuarioDto.password)) {
            apiResponse.mergeContentResponse(usuarioService.getLogin(usuarioDto.id))
            apiResponse.addStatusCode(200)
            apiResponse.addStatus("message", "Login successful")
        } else {
            apiResponse.addStatusCode(403)
            apiResponse.addStatus("message", "Intente nuevamente")
        }
        return createResponseEntity(apiResponse, rc)
    }

    @Path("/restore")
    @PATCH
    @PermitAll
    suspend fun restore(@Valid restoreDto: RestoreDto, rc: RoutingContext): Response {
        val apiResponse = createResponse()
        apiResponse.mergeContentResponse(usuarioService.changePassword(restoreDto))
        if (apiResponse.hasErrors()) {
            apiResponse.addStatusCode(400)
        } else {
            apiResponse.addStatusCode(200)
        }
        return createResponseEntity(apiResponse, rc)
    }

    @GET
    suspend fun profile(rc: RoutingContext): Response {
        val apiResponse = createResponse()
        try {
            val id = checkNotNull(jwt.subject) { "Jwt subject is null" }
            apiResponse.mergeContentResponse(usuarioService.getProfile(UUID.fromString(id)))
            apiResponse.addStatusCode(200)
        } catch (e: IllegalArgumentException) {
            log.error("Error while user fetching the profile", e)
            apiResponse.addStatusCode(403)
        }
        return createResponseEntity(apiResponse, rc)
    }
}
