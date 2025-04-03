package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.modules.common.dto.IdNombreDto;
import io.github.kingg22.api.vacunas.panama.modules.doctor.entity.Doctor;
import io.github.kingg22.api.vacunas.panama.modules.doctor.entity.DoctorKonverterKt;
import io.github.kingg22.api.vacunas.panama.modules.doctor.service.IDoctorService;
import io.github.kingg22.api.vacunas.panama.modules.fabricante.entity.Fabricante;
import io.github.kingg22.api.vacunas.panama.modules.fabricante.entity.FabricanteKonverterKt;
import io.github.kingg22.api.vacunas.panama.modules.fabricante.service.IFabricanteService;
import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente;
import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.PacienteKonverterKt;
import io.github.kingg22.api.vacunas.panama.modules.paciente.service.IPacienteService;
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona;
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.PersonaKonverterKt;
import io.github.kingg22.api.vacunas.panama.modules.persona.service.IPersonaService;
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RegisterUser;
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RestoreDto;
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RolesEnum;
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.UsuarioDto;
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Permiso;
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Rol;
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Usuario;
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.UsuarioKonverterKt;
import io.github.kingg22.api.vacunas.panama.modules.usuario.repository.PermisoRepository;
import io.github.kingg22.api.vacunas.panama.modules.usuario.repository.RolRepository;
import io.github.kingg22.api.vacunas.panama.modules.usuario.repository.UsuarioRepository;
import io.github.kingg22.api.vacunas.panama.modules.usuario.service.ITokenService;
import io.github.kingg22.api.vacunas.panama.modules.usuario.service.IUsuarioManagementService;
import io.github.kingg22.api.vacunas.panama.response.*;
import io.github.kingg22.api.vacunas.panama.service.UsuarioValidationService.RegistrationError;
import io.github.kingg22.api.vacunas.panama.service.UsuarioValidationService.RegistrationSuccess;
import io.github.kingg22.api.vacunas.panama.util.FormatterUtil;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

/**
 * Service for {@link Usuario}, {@link Rol} and {@link Permiso} Extends functionality from {@link PersonaService},
 * {@link PacienteService}, {@link DoctorService} and {@link FabricanteService} inheriting methods that involve
 * {@link Usuario} in relation to these services.
 */
@Service
public class UsuarioManagementService implements IUsuarioManagementService {
    private final UsuarioRepository usuarioRepository;
    private final PermisoRepository permisoRepository;
    private final RolRepository rolRepository;
    private final IPacienteService pacienteService;
    private final IDoctorService doctorService;
    private final ITokenService tokenService;
    private final IPersonaService personaService;
    private final IFabricanteService fabricanteService;
    private final UsuarioValidationService validationService;
    private final UsuarioTransactionService transactionService;
    private static final String PERSONA = "persona";
    private static final String PACIENTE = "paciente";
    private static final String FABRICANTE = "fabricante";
    private static final String DOCTOR = "doctor";
    private static final Logger log = LoggerFactory.getLogger(UsuarioManagementService.class);

    public UsuarioManagementService(
            UsuarioRepository usuarioRepository,
            PermisoRepository permisoRepository,
            RolRepository rolRepository,
            IPacienteService pacienteService,
            IDoctorService doctorService,
            ITokenService tokenService,
            IPersonaService personaService,
            IFabricanteService fabricanteService,
            UsuarioValidationService validationService,
            UsuarioTransactionService transactionService) {
        this.usuarioRepository = usuarioRepository;
        this.permisoRepository = permisoRepository;
        this.rolRepository = rolRepository;
        this.pacienteService = pacienteService;
        this.doctorService = doctorService;
        this.tokenService = tokenService;
        this.personaService = personaService;
        this.fabricanteService = fabricanteService;
        this.validationService = validationService;
        this.transactionService = transactionService;
    }

    @org.jetbrains.annotations.NotNull
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

    @org.jetbrains.annotations.NotNull
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

    @org.jetbrains.annotations.NotNull
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

    @org.jetbrains.annotations.NotNull
    @Cacheable(cacheNames = "short", key = "'login:' + #idUser")
    public Map<String, Serializable> setLoginData(@org.jetbrains.annotations.NotNull final UUID idUser) {
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

    @org.jetbrains.annotations.NotNull
    @Cacheable(cacheNames = "short", key = "'profile:' + #idUser")
    public Map<String, Serializable> getProfile(@org.jetbrains.annotations.NotNull final UUID idUser) {
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

    @org.jetbrains.annotations.NotNull
    @Cacheable(cacheNames = "massive", key = "'roles'")
    public List<IdNombreDto> getIdNombreRoles() {
        return rolRepository.findAllIdNombre();
    }

    @org.jetbrains.annotations.NotNull
    @Cacheable(cacheNames = "massive", key = "'permisos'")
    public List<IdNombreDto> getIdNombrePermisos() {
        return permisoRepository.findAllIdNombre();
    }

    @org.jetbrains.annotations.NotNull
    public Map<String, Serializable> generateTokens(@org.jetbrains.annotations.NotNull final UUID idUser) {
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

    public void updateLastUsed(@org.jetbrains.annotations.NotNull final UUID id) {
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
    @org.jetbrains.annotations.NotNull
    public Optional<Usuario> getUsuario(@org.jetbrains.annotations.NotNull @NotNull final String identifier) {
        log.debug("Searching by username: {}", identifier);
        var usuarioOpt = this.usuarioRepository.findByUsername(identifier);
        usuarioOpt.ifPresent(usuario -> {
            assert usuario.getId() != null;
            var personaOpt = this.personaService.getPersonaByUserID(usuario.getId());
            personaOpt.ifPresent(persona -> usuario.setDisabled(persona.getDisabled()));
            log.debug("Found user: {}, with username", usuario.getId());
        });
        if (usuarioOpt.isEmpty()) {
            var result = FormatterUtil.formatToSearch(identifier);
            log.debug(
                    "Searching by cedula: {}, pasaporte: {}, correo: {}",
                    result.cedula(),
                    result.pasaporte(),
                    result.correo());
            usuarioOpt = this.usuarioRepository.findByCedulaOrPasaporteOrCorreo(
                    result.cedula(), result.pasaporte(), result.correo());
            usuarioOpt.ifPresent(usuario -> {
                assert usuario.getId() != null;
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
                assert usuario.getId() != null;
                this.fabricanteService
                        .getFabricanteByUserID(usuario.getId())
                        .ifPresent(fabricante -> usuario.setDisabled(fabricante.getDisabled()));
                log.debug("Found user: {}, with credentials of Fabricante", usuario.getId());
            });
        }

        return usuarioOpt;
    }
}
