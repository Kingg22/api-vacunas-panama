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
import io.github.kingg22.api.vacunas.panama.response.ApiFailed;
import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode;
import io.github.kingg22.api.vacunas.panama.response.IApiContentResponse;
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
    private final TokenService tokenService;
    private final PersonaService personaService;
    private final PacienteService pacienteService;
    private final DoctorService doctorService;
    private final FabricanteService fabricanteService;
    private final UsuarioValidationService validationService;
    private final UsuarioTransactionService transactionService;

    public List<ApiFailed> validateAuthoritiesRegister(
            @org.jetbrains.annotations.NotNull @NotNull UsuarioDto usuarioDto,
            @org.jetbrains.annotations.NotNull @NotNull Authentication authentication) {
        List<ApiFailed> errors = new ArrayList<>();
        List<String> authenticatedAuthorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(role -> role.substring("ROLE_".length()))
                .toList();

        try {
            List<RolesEnum> authenticatedRoles =
                    authenticatedAuthorities.stream().map(RolesEnum::valueOf).toList();
            if (usuarioDto.roles() != null
                    && !usuarioDto.roles().stream()
                            .allMatch(rolDto -> this.validationService.canRegisterRole(rolDto, authenticatedRoles))) {
                errors.add(new ApiFailed(
                        ApiResponseCode.ROL_HIERARCHY_VIOLATION,
                        "roles[]",
                        "No puede asignar roles superiores a su rol máximo actual"));
            }
        } catch (IllegalArgumentException exception) {
            log.debug("Argument exception by RolesEnum: {}", exception.getMessage());
            errors.add(new ApiFailed(
                    ApiResponseCode.API_UPDATE_UNSUPPORTED,
                    "roles[]",
                    "Roles creados recientemente no son soportados para registrarse"));
        }

        if (!this.validationService.hasUserManagementPermissions(authenticatedAuthorities)) {
            errors.add(new ApiFailed(
                    ApiResponseCode.PERMISSION_DENIED, "No tienes permisos para registrar a otros usuarios"));
        }
        return errors;
    }

    public IApiContentResponse createUser(@org.jetbrains.annotations.NotNull @NotNull RegisterUser registerUser) {
        ApiContentResponse apiContentResponse = new ApiContentResponse();
        UsuarioDto usuarioDto = registerUser.usuario();
        Object validationResult = this.validationService.validateRegistration(registerUser);
        if (usuarioDto.roles() != null
                && usuarioDto.roles().stream()
                        .anyMatch(rolDto ->
                                rolDto.permisos() != null && !rolDto.permisos().isEmpty())) {
            apiContentResponse.addWarning(
                    ApiResponseCode.INFORMATION_IGNORED,
                    "roles[].permisos[]",
                    "Los permisos de los roles son ignorados al crear un usuario. Para crear o relacionar nuevos permisos a un rol debe utilizar otra opción");
        }
        if (validationResult instanceof List<?> failedList) {
            apiContentResponse.addErrors(failedList);
        }
        if (!apiContentResponse.hasErrors()) {
            switch (validationResult) {
                case Persona persona -> {
                    Usuario user = transactionService.createUser(usuarioDto, persona, null);
                    if (persona instanceof Paciente paciente) {
                        paciente.setUsuario(user);
                        apiContentResponse.addData("paciente", PacienteKonverterKt.toPacienteDto(paciente));
                    }
                    if (persona instanceof Doctor doctor) {
                        doctor.setUsuario(user);
                        apiContentResponse.addData("doctor", DoctorKonverterKt.toDoctorDto(doctor));
                    }
                }
                case Fabricante fabricante -> {
                    fabricante.setUsuario(transactionService.createUser(usuarioDto, null, fabricante));
                    apiContentResponse.addData("fabricante", FabricanteKonverterKt.toFabricanteDto(fabricante));
                }
                default ->
                    apiContentResponse.addError(
                            ApiResponseCode.API_UPDATE_UNSUPPORTED,
                            "Ha ocurrido un error posterior a la validación. No created");
            }
        }
        return apiContentResponse;
    }

    public ApiContentResponse changePassword(@org.jetbrains.annotations.NotNull @NotNull RestoreDto restoreDto) {
        ApiContentResponse apiContentResponse = new ApiContentResponse();
        Optional<Persona> opPersona = this.personaService.getPersona(restoreDto.username());
        opPersona.ifPresentOrElse(
                persona -> {
                    List<ApiFailed> failedList = this.validationService.validateChangePasswordPersona(
                            persona, restoreDto.newPassword(), restoreDto.fechaNacimiento());
                    apiContentResponse.addErrors(failedList);
                    if (failedList.isEmpty()) {
                        this.transactionService.changePasswordPersonas(persona, restoreDto.newPassword());
                        apiContentResponse.addData("status", "Contraseña cambiada con éxito");
                    }
                },
                () -> apiContentResponse.addError(
                        ApiResponseCode.NOT_FOUND,
                        "username",
                        "La persona con la identificación dada no fue encontrada"));
        return apiContentResponse;
    }

    @Cacheable(cacheNames = "short", key = "'login:' + #idUser")
    public Map<String, Serializable> setLoginData(UUID idUser) {
        Map<String, Serializable> data = new LinkedHashMap<>();
        Usuario usuario = this.usuarioRepository.findById(idUser).orElseThrow();
        if (usuario.getPersona() != null) {
            Persona persona = usuario.getPersona();
            if (persona instanceof Paciente paciente) {
                data.put("paciente", PacienteKonverterKt.toPacienteDto(paciente));
            }
            if (persona instanceof Doctor doctor) {
                data.put("doctor", DoctorKonverterKt.toDoctorDto(doctor));
            }
            // En caso de que necesites manejar la persona genérica
            if (!(persona instanceof Paciente) && !(persona instanceof Doctor)) {
                data.put("persona", PersonaKonverterKt.toPersonaDto(persona));
            }
        }
        if (usuario.getFabricante() != null) {
            data.put("fabricante", FabricanteKonverterKt.toFabricanteDto(usuario.getFabricante()));
        }
        data.putAll(this.generateTokens(idUser));
        return data;
    }

    @Cacheable(cacheNames = "short", key = "'profile:' + #idUser")
    public Map<String, Serializable> getProfile(UUID idUser) {
        Map<String, Serializable> data = new LinkedHashMap<>();
        this.pacienteService
                .getPacienteByUserID(idUser)
                .ifPresent(paciente -> data.put("paciente", PacienteKonverterKt.toPacienteDto(paciente)));
        this.doctorService
                .getDoctorByUserID(idUser)
                .ifPresent(doctor -> data.put("doctor", DoctorKonverterKt.toDoctorDto(doctor)));
        this.fabricanteService
                .getFabricanteByUserID(idUser)
                .ifPresent(fabricante -> data.put("fabricante", FabricanteKonverterKt.toFabricanteDto(fabricante)));
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

    public Map<String, Serializable> generateTokens(UUID idUser) {
        Usuario usuario = usuarioRepository.findById(idUser).orElseThrow();
        Map<String, Serializable> idsAdicionales = new HashMap<>();
        if (usuario.getPersona() != null) {
            idsAdicionales.put("persona", usuario.getPersona().getId());
        }
        if (usuario.getFabricante() != null) {
            idsAdicionales.put("fabricante", usuario.getFabricante().getId());
        }
        return tokenService.generateTokens(UsuarioKonverterKt.toUsuarioDto(usuario), idsAdicionales);
    }

    public void updateLastUsed(UUID id) {
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
    Optional<Usuario> getUsuario(@NotNull String identifier) {
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
