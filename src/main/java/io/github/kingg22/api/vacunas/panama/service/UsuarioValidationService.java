package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.modules.fabricante.service.IFabricanteService;
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona;
import io.github.kingg22.api.vacunas.panama.modules.persona.service.IPersonaService;
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RegisterUser;
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RolDto;
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RolesEnum;
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.UsuarioDto;
import io.github.kingg22.api.vacunas.panama.modules.usuario.repository.UsuarioRepository;
import io.github.kingg22.api.vacunas.panama.response.ApiError;
import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode;
import io.github.kingg22.api.vacunas.panama.response.DefaultApiError;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.password.ReactiveCompromisedPasswordChecker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** Service for {@link UsuarioManagementService} validations. */
@Slf4j
@Service
@RequiredArgsConstructor
class UsuarioValidationService {
    private final PasswordEncoder passwordEncoder;
    private final ReactiveCompromisedPasswordChecker compromisedPasswordChecker;
    private final UsuarioRepository usuarioRepository;
    private final IPersonaService personaService;
    private final IFabricanteService fabricanteService;
    private static final String NEW_PASSWORD = "new_password";

    @org.jetbrains.annotations.NotNull
    List<ApiError> validateWarningsRegistrarion(
            @org.jetbrains.annotations.NotNull @NotNull final UsuarioDto usuarioDto) {
        var apiContentResponse = new ArrayList<ApiError>();
        if (usuarioDto.roles() != null
                && usuarioDto.roles().stream()
                        .anyMatch(rolDto ->
                                rolDto.permisos() != null && !rolDto.permisos().isEmpty())) {
            apiContentResponse.add(
                    new DefaultApiError(
                            ApiResponseCode.INFORMATION_IGNORED,
                            "roles[].permisos[]",
                            "Los permisos de los roles son ignorados al crear un usuario. Para crear o relacionar nuevos permisos a un rol debe utilizar otra opción"));
        }
        if (usuarioDto.roles() != null
                && usuarioDto.roles().stream()
                        .anyMatch(rolDto -> rolDto.id() == null
                                && rolDto.nombre() != null
                                && !rolDto.nombre().isBlank())) {
            apiContentResponse.add(new DefaultApiError(
                    ApiResponseCode.NON_IDEMPOTENCE, "roles[]", "Utilice ID al realizar peticiones"));
        }
        return apiContentResponse;
    }

    RegistrationResult validateRegistration(
            @org.jetbrains.annotations.NotNull @NotNull final RegisterUser registerUser) {
        var errors = new ArrayList<ApiError>();
        var usuarioDto = registerUser.usuario();

        if (this.isUsernameRegistered(usuarioDto.username())) {
            errors.add(new DefaultApiError(
                    ApiResponseCode.ALREADY_TAKEN, "username", "El nombre de usuario ya está en uso"));
        }

        if (this.compromisedPasswordChecker.check(usuarioDto.password()).block().isCompromised()) {
            errors.add(new DefaultApiError(
                    ApiResponseCode.COMPROMISED_PASSWORD,
                    "password",
                    "La contraseña proporcionada está comprometida. Por favor use otra contraseña"));
        }

        if (!errors.isEmpty()) {
            return new RegistrationError(errors);
        }

        // validation is delegated to other specific methods depending on the role to be registered
        if (registerUser.cedula() != null || registerUser.pasaporte() != null) {
            return this.validateRegistrationPersona(registerUser);
        }
        if (usuarioDto.roles() != null
                && usuarioDto.roles().stream()
                        .anyMatch(rolDto -> rolDto != null
                                && rolDto.nombre() != null
                                && !rolDto.nombre().isBlank()
                                && rolDto.nombre().equalsIgnoreCase("FABRICANTE"))) {
            if (registerUser.licenciaFabricante() != null) {
                return this.validateRegistrationFabricante(registerUser);
            } else {
                errors.add(
                        new DefaultApiError(
                                ApiResponseCode.MISSING_INFORMATION,
                                "licencia_fabricante",
                                "Los fabricantes requieren licencia autorizada por Dirección Nacional de Farmacia y Drogas del MINSA"));
            }
        }
        errors.add(
                new DefaultApiError(
                        ApiResponseCode.MISSING_INFORMATION,
                        "Las personas requieren una identificación personal como cédula o pasaporte, los fabricantes deben usar licencia_fabricante."));
        return new RegistrationError(errors);
    }

    RegistrationResult validateRegistrationPersona(
            @org.jetbrains.annotations.NotNull @NotNull final RegisterUser registerUser) {
        var identifier = registerUser.cedula() != null ? registerUser.cedula() : registerUser.pasaporte();
        assert identifier != null;

        return this.personaService
                .getPersona(identifier)
                .map(persona -> {
                    if (!persona.getDisabled()) {
                        var user = persona.getUsuario();
                        if (user != null && user.getId() != null) {
                            log.debug("Usuario de persona: {}", user.getId());
                            return new RegistrationError(new DefaultApiError(
                                    ApiResponseCode.ALREADY_EXISTS, "La persona ya tiene un usuario registrado"));
                        } else {
                            return new RegistrationSuccess(persona);
                        }
                    } else {
                        log.debug("Persona attempting register but is disabled. ID: {}", persona.getId());
                        return new RegistrationError(
                                new DefaultApiError(ApiResponseCode.PERMISSION_DENIED, "No puede registrarse"));
                    }
                })
                .orElse(new RegistrationError(new DefaultApiError(
                        ApiResponseCode.NOT_FOUND,
                        "La persona con la identificación personal proporcionada no fue encontrado")));
    }

    RegistrationResult validateRegistrationFabricante(
            @org.jetbrains.annotations.NotNull @NotNull final RegisterUser registerUser) {
        return this.fabricanteService
                .getFabricante(registerUser.licenciaFabricante())
                .map(fabricante -> {
                    if (!fabricante.getDisabled()) {
                        var user = fabricante.getUsuario();
                        if (user != null && user.getId() != null) {
                            log.debug("Usuario de fabricante: {}", user.getId());
                            return new RegistrationError(new DefaultApiError(
                                    ApiResponseCode.ALREADY_EXISTS, "El fabricante ya tiene un usuario registrado"));
                        } else {
                            return new RegistrationSuccess(fabricante);
                        }
                    } else {
                        log.debug("Fabricante attempting register but is disabled. ID: {}", fabricante.getId());
                        return new RegistrationError(
                                new DefaultApiError(ApiResponseCode.PERMISSION_DENIED, "No puede registrarse"));
                    }
                })
                .orElse(new RegistrationError(new DefaultApiError(
                        ApiResponseCode.NOT_FOUND, "El fabricante con la licencia proporcionada no fue encontrado")));
    }

    List<ApiError> validateChangePasswordPersona(
            @org.jetbrains.annotations.NotNull @NotNull final Persona persona,
            final String newPassword,
            final LocalDate birthdate) {
        var errores = new ArrayList<ApiError>();
        if (!persona.getFechaNacimiento().toLocalDate().equals(birthdate)) {
            errores.add(new DefaultApiError(
                    ApiResponseCode.VALIDATION_FAILED, "fecha_nacimiento", "La fecha de cumpleaños no coincide"));
        }

        if (this.passwordEncoder.matches(newPassword, persona.getUsuario().getPassword())) {
            errores.add(new DefaultApiError(
                    ApiResponseCode.VALIDATION_FAILED,
                    NEW_PASSWORD,
                    "La nueva contraseña no puede ser igual a la contraseña actual"));
        }

        if (persona.getUsuario().getUsername() != null
                && newPassword.contains(persona.getUsuario().getUsername())) {
            errores.add(new DefaultApiError(
                    ApiResponseCode.VALIDATION_FAILED,
                    NEW_PASSWORD,
                    "La nueva contraseña no puede ser igual a su username"));
        }

        if (this.compromisedPasswordChecker.check(newPassword).block().isCompromised()) {
            errores.add(new DefaultApiError(
                    ApiResponseCode.VALIDATION_FAILED,
                    NEW_PASSWORD,
                    "La nueva contraseña está comprometida, utilice contraseñas seguras"));
        }
        return errores;
    }

    boolean isUsernameRegistered(final String username) {
        return username != null && usuarioRepository.findByUsername(username).isPresent();
    }

    boolean canRegisterRole(
            @org.jetbrains.annotations.NotNull final RolDto rolDto,
            @org.jetbrains.annotations.NotNull final List<RolesEnum> authenticatedRoles) {
        int maxRolPriority = authenticatedRoles.stream()
                .mapToInt(RolesEnum::getPriority)
                .max()
                .orElse(0);

        return RolesEnum.valueOf(rolDto.nombre().toUpperCase()).getPriority() <= maxRolPriority;
    }

    boolean hasUserManagementPermissions(
            @org.jetbrains.annotations.NotNull final List<String> authenticatedAuthorities) {
        return authenticatedAuthorities.contains("ADMINISTRATIVO_WRITE")
                || authenticatedAuthorities.contains("AUTORIDAD_WRITE")
                || authenticatedAuthorities.contains("USER_MANAGER_WRITE");
    }

    public sealed interface RegistrationResult permits RegistrationSuccess, RegistrationError {}

    @Getter
    public static final class RegistrationSuccess implements RegistrationResult {
        private final Object outcome;

        public RegistrationSuccess(final Object outcome) {
            this.outcome = outcome;
        }
    }

    @Getter
    public static final class RegistrationError implements RegistrationResult {
        private final List<ApiError> errors;

        public RegistrationError(final List<ApiError> errors) {
            this.errors = errors;
        }

        public RegistrationError(final ApiError error) {
            this.errors = List.of(error);
        }
    }
}
