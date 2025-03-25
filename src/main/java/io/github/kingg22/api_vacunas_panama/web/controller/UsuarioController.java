package io.github.kingg22.api_vacunas_panama.web.controller;

import io.github.kingg22.api_vacunas_panama.persistence.entity.Doctor;
import io.github.kingg22.api_vacunas_panama.persistence.entity.Entidad;
import io.github.kingg22.api_vacunas_panama.persistence.entity.Fabricante;
import io.github.kingg22.api_vacunas_panama.persistence.entity.Paciente;
import io.github.kingg22.api_vacunas_panama.persistence.entity.Permiso;
import io.github.kingg22.api_vacunas_panama.persistence.entity.Persona;
import io.github.kingg22.api_vacunas_panama.persistence.entity.Rol;
import io.github.kingg22.api_vacunas_panama.persistence.entity.Usuario;
import io.github.kingg22.api_vacunas_panama.response.ApiResponseCode;
import io.github.kingg22.api_vacunas_panama.response.ApiResponseFactory;
import io.github.kingg22.api_vacunas_panama.response.ApiResponseUtil;
import io.github.kingg22.api_vacunas_panama.response.IApiContentResponse;
import io.github.kingg22.api_vacunas_panama.response.IApiResponse;
import io.github.kingg22.api_vacunas_panama.service.IUsuarioManagementService;
import io.github.kingg22.api_vacunas_panama.util.RolesEnum;
import io.github.kingg22.api_vacunas_panama.web.dto.LoginDto;
import io.github.kingg22.api_vacunas_panama.web.dto.RegisterUser;
import io.github.kingg22.api_vacunas_panama.web.dto.RestoreDto;
import io.github.kingg22.api_vacunas_panama.web.dto.UsuarioDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.password.CompromisedPasswordException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.Serializable;
import java.util.UUID;

/**
 * Controller for {@link Usuario} registration and management, {@link Rol} and {@link Permiso}.
 * <p>
 * This controller handles operations related to registering users and managing their roles and associated entities
 * (e.g., {@link Paciente}, {@link Doctor}, {@link Fabricante}). It ensures that users are linked to an existing {@link Persona} or {@link Entidad}
 * and properly assigned roles.
 * </p>
 *
 * <p><b>Response Format:</b> The response for registration and related endpoints typically includes:
 * <ul>
 *   <li>User details (e.g., username, roles, etc.).</li>
 *   <li>Associated {@link Persona} or {@link Entidad} information (e.g., {@link Paciente}, {@link Doctor}, {@link Fabricante})
 *   if applicable.</li>
 *   <li>A JWT token, which is only generated if the associated persona or entity has an active (validated) status.</li>
 * </ul>
 * </p>
 * <br>
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/vacunacion/v1/account", produces = MediaType.APPLICATION_JSON_VALUE)
public class UsuarioController {
    private final AuthenticationManager authenticationManager;
    private final IUsuarioManagementService usuarioManagementService;
    private final ApiResponseFactory apiResponseFactory;

    /**
     * Handles user registration.
     * <p>
     * First, it checks if the current {@link Authentication} is for an authenticated user with sufficient permissions
     * to create users with lower {@link Rol}. If not authenticated, it allows registering a {@link Paciente}.
     * It also validates tha data to be registered (e.g., username, email) is not currently in use.
     * <p>
     * If all validations pass, the {@link Usuario} is created.
     * <p><b>Note:</b> The user must be assigned roles, and empty roles are not allowed.
     * If the associated entities is not created, the request will be rejected.
     * For cases where both the {@link Persona}/{@link Entidad} and the {@link Usuario} need to created in a single request,
     * a different endpoint should be used.</p>
     *
     * @param registerUser   The {@link RegisterUser} containing the user registration details.
     * @param authentication The {@link Authentication} representing the current user (if any).
     * @param request        The {@link ServletWebRequest} used for building the response.
     * @return {@link IApiResponse} containing the registration result, including user details, associated {@link Persona} or {@link Entidad}
     * information and a token if the {@link Persona} or {@link Entidad} is validated and active.
     */
    @PostMapping({"/register"})
    public ResponseEntity<IApiResponse<String, Serializable>> register(@RequestBody @Valid RegisterUser registerUser,
                                                                       Authentication authentication,
                                                                       ServletWebRequest request) {
        IApiResponse<String, Serializable> apiResponse = apiResponseFactory.createResponse();
        UsuarioDto usuarioDto = registerUser.usuario();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            var failedList = this.usuarioManagementService.validateAuthoritiesRegister(usuarioDto, authentication);
            apiResponse.addErrors(failedList);
        } else if (!usuarioDto.roles().stream().allMatch(rolDto -> RolesEnum.getByPriority(rolDto.id()).equals(RolesEnum.PACIENTE) ||
                rolDto.nombre() != null && rolDto.nombre().equalsIgnoreCase("Paciente"))) {
            apiResponse.addError(ApiResponseCode.MISSING_ROLE_OR_PERMISSION, "Solo pacientes pueden registrarse sin autenticación");
        }

        if (apiResponse.hasErrors()) {
            apiResponse.addStatusCode(HttpStatus.FORBIDDEN);
            apiResponse.addStatus("Insufficient authorities");
        } else {
            var apiContentResponse = this.usuarioManagementService.createUser(registerUser);
            apiResponse.addWarnings(apiContentResponse.getWarnings());
            apiResponse.addErrors(apiContentResponse.getErrors());
            apiResponse.addData(apiContentResponse.getData());
            if (apiContentResponse.hasErrors()) {
                apiResponse.addStatusCode(HttpStatus.BAD_REQUEST);
            } else {
                apiResponse.addStatusCode(HttpStatus.CREATED);
            }
        }
        return ApiResponseUtil.sendResponse(apiResponse, request);
    }

    @PostMapping({"/login"})
    public ResponseEntity<IApiResponse<String, Serializable>> login(@RequestBody @Valid LoginDto loginDto, ServletWebRequest request) {
        IApiResponse<String, Serializable> apiResponse = apiResponseFactory.createResponse();
        Authentication authentication = null;

        try {
            authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password()));
        } catch (CompromisedPasswordException exception) {
            log.debug("CompromisedPassword: {}", exception.getMessage());
            apiResponse.addStatusCode(HttpStatus.TEMPORARY_REDIRECT);
            apiResponse.addStatus("Please reset your password in the given uri");
            apiResponse.addStatus("/vacunacion/v1/account/restore");
            apiResponse.addError(ApiResponseCode.COMPROMISED_PASSWORD, "password", "Su contraseña está comprometida, por favor cambiarla lo más pronto posible");
        }

        if (authentication != null && authentication.isAuthenticated()) {
            apiResponse.addData(this.usuarioManagementService.setLoginData(UUID.fromString(authentication.getName())));
            apiResponse.addStatusCode(HttpStatus.OK);
            apiResponse.addStatus("Login successful");
        }

        return ApiResponseUtil.sendResponse(apiResponse, request);
    }

    @PatchMapping({"/restore"})
    public ResponseEntity<IApiResponse<String, Serializable>> restore(@RequestBody @Valid RestoreDto restoreDto,
                                                                      ServletWebRequest request) {
        IApiResponse<String, Serializable> apiResponse = apiResponseFactory.createResponse();
        IApiContentResponse apiContentResponse = this.usuarioManagementService.changePassword(restoreDto);
        if (apiContentResponse.hasErrors()) {
            apiResponse.addStatusCode(HttpStatus.BAD_REQUEST);
            apiResponse.addErrors(apiContentResponse.getErrors());
        } else {
            apiResponse.addStatusCode(HttpStatus.OK);
            apiResponse.addData(apiContentResponse.getData());
        }
        return ApiResponseUtil.sendResponse(apiResponse, request);
    }

    @GetMapping
    public ResponseEntity<IApiResponse<String, Serializable>> profile(Authentication authentication,
                                                                      ServletWebRequest request) {
        IApiResponse<String, Serializable> apiResponse = apiResponseFactory.createResponse();
        apiResponse.addData(usuarioManagementService.getProfile(UUID.fromString(authentication.getName())));
        apiResponse.addStatusCode(HttpStatus.OK);
        return ApiResponseUtil.sendResponse(apiResponse, request);
    }

}
