package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Doctor;
import io.github.kingg22.api.vacunas.panama.persistence.entity.DoctorKonverterKt;
import io.github.kingg22.api.vacunas.panama.persistence.entity.Fabricante;
import io.github.kingg22.api.vacunas.panama.persistence.entity.FabricanteKonverterKt;
import io.github.kingg22.api.vacunas.panama.persistence.entity.Paciente;
import io.github.kingg22.api.vacunas.panama.persistence.entity.PacienteKonverterKt;
import io.github.kingg22.api.vacunas.panama.persistence.entity.Permiso;
import io.github.kingg22.api.vacunas.panama.persistence.entity.Persona;
import io.github.kingg22.api.vacunas.panama.persistence.entity.PersonaKonverterKt;
import io.github.kingg22.api.vacunas.panama.persistence.entity.Rol;
import io.github.kingg22.api.vacunas.panama.persistence.entity.Usuario;
import io.github.kingg22.api.vacunas.panama.persistence.entity.UsuarioKonverterKt;
import io.github.kingg22.api.vacunas.panama.persistence.repository.PermisoRepository;
import io.github.kingg22.api.vacunas.panama.persistence.repository.RolRepository;
import io.github.kingg22.api.vacunas.panama.persistence.repository.UsuarioRepository;
import io.github.kingg22.api.vacunas.panama.response.ApiContentResponse;
import io.github.kingg22.api.vacunas.panama.response.ApiError;
import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode;
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory;
import io.github.kingg22.api.vacunas.panama.response.DefaultApiError;
import io.github.kingg22.api.vacunas.panama.service.UsuarioValidationService.RegistrationError;
import io.github.kingg22.api.vacunas.panama.service.UsuarioValidationService.RegistrationSuccess;
import io.github.kingg22.api.vacunas.panama.util.FormatterUtil;
import io.github.kingg22.api.vacunas.panama.util.RolesEnum;
import io.github.kingg22.api.vacunas.panama.web.dto.IdNombreDto;
import io.github.kingg22.api.vacunas.panama.web.dto.RegisterUser;
import io.github.kingg22.api.vacunas.panama.web.dto.RestoreDto;
import io.github.kingg22.api.vacunas.panama.web.dto.UsuarioDto;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

/**
 * Service for {@link Usuario}, {@link Rol} and {@link Permiso} Extends functionality from {@link PersonaService},
 * {@link PacienteService}, {@link DoctorService} and {@link FabricanteService} inheriting methods that involve
 * {@link Usuario} in relation to these services.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioManagementService implements IUsuarioManagementService {
    private final UsuarioRepository usuarioRepository;
    private final PermisoRepository permisoRepository;
    private final RolRepository rolRepository;
    private final IPacienteService pacienteService;
    private final TokenService tokenService;
    private final PersonaService personaService;
    private final DoctorService doctorService;
    private final FabricanteService fabricanteService;
    private final UsuarioValidationService validationService;
    private final UsuarioTransactionService transactionService;
    private static final String PERSONA = "persona";
    private static final String PACIENTE = "paciente";
    private static final String FABRICANTE = "fabricante";
    private static final String DOCTOR = "doctor";

    public List<ApiError> validateAuthoritiesRegister(
            @org.jetbrains.annotations.NotNull @NotNull final UsuarioDto usuarioDto,
            @org.jetbrains.annotations.NotNull @NotNull final Authentication authentication) {
        var errors = new ArrayList<ApiError>();
        var authenticatedAuthorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(role -> role.substring("ROLE_".length()))
                .toList();

        try {
            var authenticatedRoles =
                    authenticatedAuthorities.stream().map(RolesEnum::valueOf).toList();
            if (usuarioDto.roles() != null
                    && !usuarioDto.roles().stream()
                            .allMatch(rolDto -> this.validationService.canRegisterRole(rolDto, authenticatedRoles))) {
                errors.add(new DefaultApiError(
                        ApiResponseCode.ROL_HIERARCHY_VIOLATION,
                        "roles[]",
                        "No puede asignar roles superiores a su rol máximo actual"));
            }
        } catch (IllegalArgumentException exception) {
            log.debug("Argument exception by RolesEnum: {}", exception.getMessage());
            errors.add(new DefaultApiError(
                    ApiResponseCode.API_UPDATE_UNSUPPORTED,
                    "roles[]",
                    "Roles creados recientemente no son soportados para registrarse"));
        }

        if (!this.validationService.hasUserManagementPermissions(authenticatedAuthorities)) {
            errors.add(new DefaultApiError(
                    ApiResponseCode.PERMISSION_DENIED, "No tienes permisos para registrar a otros usuarios"));
        }
        return errors;
    }

    public ApiContentResponse createUser(@org.jetbrains.annotations.NotNull @NotNull final RegisterUser registerUser) {
        var apiContentResponse = ApiResponseFactory.createContentResponse();
        var usuarioDto = registerUser.usuario();
        apiContentResponse.addWarnings(this.validationService.validateWarningsRegistrarion(usuarioDto));
        var validationResult = this.validationService.validateRegistration(registerUser);
        if (validationResult instanceof RegistrationError error) {
            apiContentResponse.addErrors(error.getErrors());
        }
        if (!apiContentResponse.hasErrors() && validationResult instanceof RegistrationSuccess success) {
            switch (success.getOutcome()) {
                case Persona persona -> {
                    var user = transactionService.createUser(usuarioDto, persona, null);
                    if (persona instanceof Paciente paciente) {
                        paciente.setUsuario(user);
                        apiContentResponse.addData(PACIENTE, PacienteKonverterKt.toPacienteDto(paciente));
                    }
                    if (persona instanceof Doctor doctor) {
                        doctor.setUsuario(user);
                        apiContentResponse.addData(DOCTOR, DoctorKonverterKt.toDoctorDto(doctor));
                    }
                    persona.setUsuario(user);
                    apiContentResponse.addData(PERSONA, PersonaKonverterKt.toPersonaDto(persona));
                }
                case Fabricante fabricante -> {
                    fabricante.setUsuario(transactionService.createUser(usuarioDto, null, fabricante));
                    apiContentResponse.addData(FABRICANTE, FabricanteKonverterKt.toFabricanteDto(fabricante));
                }
                default ->
                    apiContentResponse.addError(new DefaultApiError(
                            ApiResponseCode.API_UPDATE_UNSUPPORTED,
                            "Ha ocurrido un error posterior a la validación. No created"));
            }
        }
        return apiContentResponse;
    }

    public ApiContentResponse changePassword(@org.jetbrains.annotations.NotNull @NotNull final RestoreDto restoreDto) {
        var apiContentResponse = ApiResponseFactory.createContentResponse();
        var opPersona = this.personaService.getPersona(restoreDto.username());
        opPersona.ifPresentOrElse(
                persona -> {
                    var failedList = this.validationService.validateChangePasswordPersona(
                            persona, restoreDto.newPassword(), restoreDto.fechaNacimiento());
                    apiContentResponse.addErrors(failedList);
                    if (failedList.isEmpty()) {
                        this.transactionService.changePasswordPersonas(persona, restoreDto.newPassword());
                        apiContentResponse.addData("status", "Contraseña cambiada con éxito");
                    }
                },
                () -> apiContentResponse.addError(new DefaultApiError(
                        ApiResponseCode.NOT_FOUND,
                        "username",
                        "La persona con la identificación dada no fue encontrada")));
        return apiContentResponse;
    }

    @Cacheable(cacheNames = "short", key = "'login:' + #idUser")
    public Map<String, Serializable> setLoginData(final UUID idUser) {
        Map<String, Serializable> data = new LinkedHashMap<>();
        Usuario usuario = this.usuarioRepository.findById(idUser).orElseThrow();
        if (usuario.getPersona() != null) {
            Persona persona = usuario.getPersona();
            if (persona instanceof Paciente paciente) {
                data.put(PACIENTE, PacienteKonverterKt.toPacienteDto(paciente));
            }
            if (persona instanceof Doctor doctor) {
                data.put(DOCTOR, DoctorKonverterKt.toDoctorDto(doctor));
            }
            // En caso de que necesites manejar la persona genérica
            if (!(persona instanceof Paciente) && !(persona instanceof Doctor)) {
                data.put(PERSONA, PersonaKonverterKt.toPersonaDto(persona));
            }
        }
        if (usuario.getFabricante() != null) {
            data.put(FABRICANTE, FabricanteKonverterKt.toFabricanteDto(usuario.getFabricante()));
        }
        data.putAll(this.generateTokens(idUser));
        return data;
    }

    @Cacheable(cacheNames = "short", key = "'profile:' + #idUser")
    public Map<String, Serializable> getProfile(final UUID idUser) {
        Map<String, Serializable> data = new LinkedHashMap<>();
        this.pacienteService
                .getPacienteByUserID(idUser)
                .ifPresent(paciente -> data.put(PACIENTE, PacienteKonverterKt.toPacienteDto(paciente)));
        this.doctorService
                .getDoctorByUserID(idUser)
                .ifPresent(doctor -> data.put(DOCTOR, DoctorKonverterKt.toDoctorDto(doctor)));
        this.fabricanteService
                .getFabricanteByUserID(idUser)
                .ifPresent(fabricante -> data.put(FABRICANTE, FabricanteKonverterKt.toFabricanteDto(fabricante)));
        return data;
    }

    @Cacheable(cacheNames = "massive", key = "'roles'")
    public List<IdNombreDto> getIdNombreRoles() {
        return rolRepository.findAllIdNombre();
    }

    @Cacheable(cacheNames = "massive", key = "'permisos'")
    public List<IdNombreDto> getIdNombrePermisos() {
        return permisoRepository.findAllIdNombre();
    }

    public Map<String, Serializable> generateTokens(final UUID idUser) {
        Usuario usuario = usuarioRepository.findById(idUser).orElseThrow();
        Map<String, Serializable> idsAdicionales = new HashMap<>();
        if (usuario.getPersona() != null) {
            idsAdicionales.put(PERSONA, usuario.getPersona().getId());
        }
        if (usuario.getFabricante() != null) {
            idsAdicionales.put(FABRICANTE, usuario.getFabricante().getId());
        }
        return tokenService.generateTokens(UsuarioKonverterKt.toUsuarioDto(usuario), idsAdicionales);
    }

    public void updateLastUsed(final UUID id) {
        this.transactionService.updateLastUsed(id);
    }

    /**
     * Finds a user based on a given identifier by searching across multiple fields form related tables.
     *
     * <p>Additionally, the user's {@code disabled} status is manually set as part of the result.
     *
     * @param identifier the identifier used to search for the user (e.g. username, email, cedula)
     * @return an {@link Optional} containing the found {@link Usuario} or empty if no user matches the identifier.
     */
    public Optional<Usuario> getUsuario(@NotNull final String identifier) {
        log.debug("Searching by username: {}", identifier);
        Optional<Usuario> usuarioOpt = this.usuarioRepository.findByUsername(identifier);
        usuarioOpt.ifPresent(usuario -> {
            Optional<Persona> personaOpt = this.personaService.getPersonaByUserID(usuario.getId());
            personaOpt.ifPresent(persona -> usuario.setDisabled(persona.getDisabled()));
            log.debug("Found user: {}, with username", usuario.getId());
        });
        if (usuarioOpt.isEmpty()) {
            FormatterUtil.FormatterResult result = FormatterUtil.formatToSearch(identifier);
            log.debug(
                    "Searching by cedula: {}, pasaporte: {}, correo: {}",
                    result.cedula(),
                    result.pasaporte(),
                    result.correo());
            usuarioOpt = this.usuarioRepository.findByCedulaOrPasaporteOrCorreo(
                    result.cedula(), result.pasaporte(), result.correo());
            usuarioOpt.ifPresent(usuario -> {
                this.personaService
                        .getPersonaByUserID(usuario.getId())
                        .ifPresent(persona -> usuario.setDisabled(persona.getDisabled()));
                log.debug("Found user: {}, with credentials of Persona", usuario.getId());
            });
        }

        if (usuarioOpt.isEmpty()) {
            log.debug("Searching by licencia or correo Fabricante");
            usuarioOpt = this.usuarioRepository.findByLicenciaOrCorreo(identifier, identifier);
            usuarioOpt.ifPresent(usuario -> {
                this.fabricanteService
                        .getFabricanteByUserID(usuario.getId())
                        .ifPresent(fabricante -> usuario.setDisabled(fabricante.getDisabled()));
                log.debug("Found user: {}, with credentials of Fabricante", usuario.getId());
            });
        }

        return usuarioOpt;
    }
}
