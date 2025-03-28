package io.github.kingg22.api.vacunas.panama.web.controller;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Doctor;
import io.github.kingg22.api.vacunas.panama.persistence.entity.Entidad;
import io.github.kingg22.api.vacunas.panama.persistence.entity.Fabricante;
import io.github.kingg22.api.vacunas.panama.persistence.entity.Paciente;
import io.github.kingg22.api.vacunas.panama.persistence.entity.Permiso;
import io.github.kingg22.api.vacunas.panama.persistence.entity.Persona;
import io.github.kingg22.api.vacunas.panama.persistence.entity.Rol;
import io.github.kingg22.api.vacunas.panama.persistence.entity.Usuario;
import io.github.kingg22.api.vacunas.panama.response.ApiResponse;
import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode;
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory;
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil;
import io.github.kingg22.api.vacunas.panama.response.DefaultApiError;
import io.github.kingg22.api.vacunas.panama.service.IUsuarioManagementService;
import io.github.kingg22.api.vacunas.panama.util.RolesEnum;
import io.github.kingg22.api.vacunas.panama.web.dto.LoginDto;
import io.github.kingg22.api.vacunas.panama.web.dto.RegisterUser;
import io.github.kingg22.api.vacunas.panama.web.dto.RestoreDto;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.password.CompromisedPasswordException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * Controller for {@link Usuario} registration and management, {@link Rol} and {@link Permiso}.
 *
 * <p>This controller handles operations related to registering users and managing their roles and associated entities
 * (e.g., {@link Paciente}, {@link Doctor}, {@link Fabricante}). It ensures that users are linked to an existing
 * {@link Persona} or {@link Entidad} and properly assigned roles.
 *
 * <p><b>Response Format:</b> The response for registration and related endpoints typically includes:
 *
 * <ul>
 *   <li>User details (e.g., username, roles, etc.).
 *   <li>Associated {@link Persona} or {@link Entidad} information (e.g., {@link Paciente}, {@link Doctor},
 *       {@link Fabricante}) if applicable.
 *   <li>A JWT token, which is only generated if the associated persona or entity has an active (validated) status.
 * </ul>
 *
 * <br>
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/vacunacion/v1/account", produces = MediaType.APPLICATION_JSON_VALUE)
public class UsuarioController {
    private final AuthenticationManager authenticationManager;
    private final IUsuarioManagementService usuarioManagementService;

    /**
     * Handles user registration.
     *
     * <p>First, it checks if the current {@link Authentication} is for an authenticated user with sufficient
     * permissions to create users with lower {@link Rol}. If not authenticated, it allows registering a
     * {@link Paciente}. It also validates tha data to be registered (e.g., username, email) is not currently in use.
     *
     * <p>If all validations pass, the {@link Usuario} is created.
     *
     * <p><b>Note:</b> The user must be assigned roles, and empty roles are not allowed. If the associated entities is
     * not created, the request will be rejected. For cases where both the {@link Persona}/{@link Entidad} and the
     * {@link Usuario} need to created in a single request, a different endpoint should be used.
     *
     * @param registerUser The {@link RegisterUser} containing the user registration details.
     * @param authentication The {@link Authentication} representing the current user (if any).
     * @param request The {@link ServletWebRequest} used for building the response.
     * @return {@link ApiResponse} containing the registration result, including user details, associated
     *     {@link Persona} or {@link Entidad} information and a token if the {@link Persona} or {@link Entidad} is
     *     validated and active.
     */
    @PostMapping({"/register"})
    public ResponseEntity<ApiResponse> register(
            @RequestBody @Valid RegisterUser registerUser, Authentication authentication, ServletWebRequest request) {
        var apiResponse = ApiResponseFactory.createResponse();
        var usuarioDto = registerUser.usuario();
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            var failedList = this.usuarioManagementService.validateAuthoritiesRegister(usuarioDto, authentication);
            apiResponse.addErrors(failedList);
        } else if (usuarioDto.roles() != null
                && !usuarioDto.roles().stream()
                        .allMatch(rolDto -> rolDto.id() != null
                                        && RolesEnum.getByPriority(rolDto.id()).equals(RolesEnum.PACIENTE)
                                || rolDto.nombre() != null && rolDto.nombre().equalsIgnoreCase("Paciente"))) {
            apiResponse.addError(new DefaultApiError(
                    ApiResponseCode.MISSING_ROLE_OR_PERMISSION,
                    "Solo pacientes pueden registrarse sin autenticación"));
        }

        if (apiResponse.hasErrors()) {
            apiResponse.addStatusCode(HttpStatus.FORBIDDEN);
            apiResponse.addStatus("message", ApiResponseCode.INSUFFICIENT_ROLE_PRIVILEGES);
            return ApiResponseUtil.sendResponse(apiResponse, request);
        }

        var apiContentResponse = this.usuarioManagementService.createUser(registerUser);
        apiResponse.mergeContentResponse(apiContentResponse);
        if (apiContentResponse.hasErrors()) {
            apiResponse.addStatusCode(HttpStatus.BAD_REQUEST);
        } else {
            apiResponse.addStatusCode(HttpStatus.CREATED);
        }
        return ApiResponseUtil.sendResponse(apiResponse, request);
    }

    @PostMapping({"/login"})
    public ResponseEntity<ApiResponse> login(@RequestBody @Valid LoginDto loginDto, ServletWebRequest request) {
        var apiResponse = ApiResponseFactory.createResponse();
        Authentication authentication = null;

        try {
            authentication = this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password()));
        } catch (CompromisedPasswordException exception) {
            log.debug("CompromisedPassword: {}", exception.getMessage());
            apiResponse.addStatusCode(HttpStatus.TEMPORARY_REDIRECT);
            apiResponse.addStatus("Please reset your password in the given uri", "/vacunacion/v1/account/restore");
            apiResponse.addError(new DefaultApiError(
                    ApiResponseCode.COMPROMISED_PASSWORD,
                    "password",
                    "Su contraseña está comprometida, por favor cambiarla lo más pronto posible"));
        }

        if (authentication != null && authentication.isAuthenticated()) {
            apiResponse.addData(this.usuarioManagementService.setLoginData(UUID.fromString(authentication.getName())));
            apiResponse.addStatusCode(HttpStatus.OK);
            apiResponse.addStatus("message", "Login successful");
        }

        return ApiResponseUtil.sendResponse(apiResponse, request);
    }

    @PatchMapping({"/restore"})
    public ResponseEntity<ApiResponse> restore(@RequestBody @Valid RestoreDto restoreDto, ServletWebRequest request) {
        var apiResponse = ApiResponseFactory.createResponse();
        var apiContentResponse = this.usuarioManagementService.changePassword(restoreDto);
        apiResponse.mergeContentResponse(apiContentResponse);
        if (apiContentResponse.hasErrors()) {
            apiResponse.addStatusCode(HttpStatus.BAD_REQUEST);
        } else {
            apiResponse.addStatusCode(HttpStatus.OK);
        }
        return ApiResponseUtil.sendResponse(apiResponse, request);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> profile(@NotNull Authentication authentication, ServletWebRequest request) {
        var apiResponse = ApiResponseFactory.createResponse();
        apiResponse.addData(usuarioManagementService.getProfile(UUID.fromString(authentication.getName())));
        apiResponse.addStatusCode(HttpStatus.OK);
        return ApiResponseUtil.sendResponse(apiResponse, request);
    }
}
