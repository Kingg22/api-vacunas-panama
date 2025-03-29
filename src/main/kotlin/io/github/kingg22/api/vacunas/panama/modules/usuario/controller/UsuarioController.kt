package io.github.kingg22.api.vacunas.panama.modules.usuario.controller

import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.LoginDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RegisterUser
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RestoreDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RolDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RolesEnum
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RolesEnum.Companion.getByPriority
import io.github.kingg22.api.vacunas.panama.modules.usuario.service.IUsuarioManagementService
import io.github.kingg22.api.vacunas.panama.response.ApiResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.sendResponse
import io.github.kingg22.api.vacunas.panama.response.DefaultApiError
import io.github.kingg22.api.vacunas.panama.util.logger
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.password.CompromisedPasswordException
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.ServletWebRequest
import java.util.UUID

/**
 * Controller for [io.github.kingg22.api.vacunas.panama.persistence.entity.Usuario] registration and management, [io.github.kingg22.api.vacunas.panama.persistence.entity.Rol] and [io.github.kingg22.api.vacunas.panama.persistence.entity.Permiso].
 *
 *
 * This controller handles operations related to registering users and managing their roles and associated entities
 * (e.g., [io.github.kingg22.api.vacunas.panama.persistence.entity.Paciente], [io.github.kingg22.api.vacunas.panama.persistence.entity.Doctor], [io.github.kingg22.api.vacunas.panama.persistence.entity.Fabricante]). It ensures that users are linked to an existing
 * [io.github.kingg22.api.vacunas.panama.persistence.entity.Persona] or [io.github.kingg22.api.vacunas.panama.persistence.entity.Entidad] and properly assigned roles.
 *
 *
 * **Response Format:** The response for registration and related endpoints typically includes:
 *
 *
 *  * User details (e.g., username, roles, etc.).
 *  * Associated [io.github.kingg22.api.vacunas.panama.persistence.entity.Persona] or [io.github.kingg22.api.vacunas.panama.persistence.entity.Entidad] information (e.g., [io.github.kingg22.api.vacunas.panama.persistence.entity.Paciente], [io.github.kingg22.api.vacunas.panama.persistence.entity.Doctor],
 * [io.github.kingg22.api.vacunas.panama.persistence.entity.Fabricante]) if applicable.
 *  * A JWT token, which is only generated if the associated persona or entity has an active (validated) status.
 */
@RestController
@RequestMapping(path = ["/vacunacion/v1/account"], produces = [MediaType.APPLICATION_JSON_VALUE])
class UsuarioController(
    private val authenticationManager: AuthenticationManager,
    private val usuarioManagementService: IUsuarioManagementService,
) {
    private val log = logger()

    /**
     * Handles user registration.
     *
     *
     * First, it checks if the current [Authentication] is for an authenticated user with sufficient
     * permissions to create users with lower [io.github.kingg22.api.vacunas.panama.persistence.entity.Rol]. If not authenticated, it allows registering a
     * [io.github.kingg22.api.vacunas.panama.persistence.entity.Paciente]. It also validates tha data to be registered (e.g., username, email) is not currently in use.
     *
     *
     * If all validations pass, the [io.github.kingg22.api.vacunas.panama.persistence.entity.Usuario] is created.
     *
     *
     * **Note:** The user must be assigned roles, and empty roles are not allowed. If the associated entities is
     * not created, the request will be rejected. For cases where both the [io.github.kingg22.api.vacunas.panama.persistence.entity.Persona]/[io.github.kingg22.api.vacunas.panama.persistence.entity.Entidad] and the
     * [io.github.kingg22.api.vacunas.panama.persistence.entity.Usuario] need to created in a single request, a different endpoint should be used.
     *
     * @param registerUser The [RegisterUser] containing the user registration details.
     * @param authentication The [Authentication] representing the current user (if any).
     * @param request The [ServletWebRequest] used for building the response.
     * @return [ApiResponse] containing the registration result, including user details, associated
     * [io.github.kingg22.api.vacunas.panama.persistence.entity.Persona] or [io.github.kingg22.api.vacunas.panama.persistence.entity.Entidad] information and a token if the [io.github.kingg22.api.vacunas.panama.persistence.entity.Persona] or [io.github.kingg22.api.vacunas.panama.persistence.entity.Entidad] is
     * validated and active.
     */
    @PostMapping("/register")
    fun register(
        @RequestBody @Valid registerUser: RegisterUser,
        authentication: Authentication?,
        request: ServletWebRequest,
    ): ResponseEntity<ApiResponse> {
        val apiResponse = createResponse()
        val usuarioDto = registerUser.usuario
        if (authentication != null &&
            authentication.isAuthenticated &&
            authentication !is AnonymousAuthenticationToken
        ) {
            apiResponse.addErrors(usuarioManagementService.validateAuthoritiesRegister(usuarioDto, authentication))
        } else if (usuarioDto.roles != null &&
            !usuarioDto.roles.stream()
                .allMatch { rolDto: RolDto ->
                    rolDto.id != null &&
                        getByPriority(rolDto.id) == RolesEnum.PACIENTE ||
                        rolDto.nombre != null &&
                        rolDto.nombre.equals(RolesEnum.PACIENTE.name, ignoreCase = true)
                }
        ) {
            apiResponse.addError(
                DefaultApiError(
                    ApiResponseCode.MISSING_ROLE_OR_PERMISSION,
                    "Solo pacientes pueden registrarse sin autenticación",
                ),
            )
        }

        if (apiResponse.hasErrors()) {
            apiResponse.addStatusCode(HttpStatus.FORBIDDEN)
            apiResponse.addStatus("message", ApiResponseCode.INSUFFICIENT_ROLE_PRIVILEGES)
            return sendResponse(apiResponse, request)
        }

        val apiContentResponse = usuarioManagementService.createUser(registerUser)
        apiResponse.mergeContentResponse(apiContentResponse)
        if (apiContentResponse.hasErrors()) {
            apiResponse.addStatusCode(HttpStatus.BAD_REQUEST)
        } else {
            apiResponse.addStatusCode(HttpStatus.CREATED)
        }
        return sendResponse(apiResponse, request)
    }

    @PostMapping("/login")
    fun login(@RequestBody @Valid loginDto: LoginDto, request: ServletWebRequest): ResponseEntity<ApiResponse> {
        val apiResponse = createResponse()

        try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(loginDto.username, loginDto.password),
            )
            if (authentication.isAuthenticated) {
                apiResponse.addData(usuarioManagementService.setLoginData(UUID.fromString(authentication.name)))
                apiResponse.addStatusCode(HttpStatus.OK)
                apiResponse.addStatus("message", "Login successful")
            }
        } catch (exception: CompromisedPasswordException) {
            log.debug("CompromisedPassword: {}", exception.message)
            apiResponse.addStatusCode(HttpStatus.TEMPORARY_REDIRECT)
            apiResponse.addStatus(
                "Please reset your password in the given uri",
                "/vacunacion/v1/account/restore",
            )
            apiResponse.addError(
                DefaultApiError(
                    ApiResponseCode.COMPROMISED_PASSWORD,
                    "password",
                    "Su contraseña está comprometida, por favor cambiarla lo más pronto posible",
                ),
            )
        }
        return sendResponse(apiResponse, request)
    }

    @PatchMapping("/restore")
    fun restore(@RequestBody @Valid restoreDto: RestoreDto, request: ServletWebRequest): ResponseEntity<ApiResponse> {
        val apiResponse = createResponse()
        apiResponse.mergeContentResponse(usuarioManagementService.changePassword(restoreDto))
        if (apiResponse.hasErrors()) {
            apiResponse.addStatusCode(HttpStatus.BAD_REQUEST)
        } else {
            apiResponse.addStatusCode(HttpStatus.OK)
        }
        return sendResponse(apiResponse, request)
    }

    @GetMapping
    fun profile(authentication: Authentication, request: ServletWebRequest): ResponseEntity<ApiResponse> {
        val apiResponse = createResponse()
        try {
            apiResponse.addData(usuarioManagementService.getProfile(UUID.fromString(authentication.name)))
            apiResponse.addStatusCode(HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            log.error("Error while user fetching the profile", e)
            apiResponse.addStatusCode(HttpStatus.FORBIDDEN)
        }
        return sendResponse(apiResponse, request)
    }
}
