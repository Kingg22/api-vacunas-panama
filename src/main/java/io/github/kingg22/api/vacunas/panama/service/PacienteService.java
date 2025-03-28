package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Direccion;
import io.github.kingg22.api.vacunas.panama.persistence.entity.Paciente;
import io.github.kingg22.api.vacunas.panama.persistence.entity.PacienteKonverterKt;
import io.github.kingg22.api.vacunas.panama.persistence.repository.PacienteRepository;
import io.github.kingg22.api.vacunas.panama.response.ApiContentResponse;
import io.github.kingg22.api.vacunas.panama.response.ApiError;
import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode;
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory;
import io.github.kingg22.api.vacunas.panama.response.DefaultApiError;
import io.github.kingg22.api.vacunas.panama.util.FormatterUtil;
import io.github.kingg22.api.vacunas.panama.util.RolesEnum;
import io.github.kingg22.api.vacunas.panama.web.dto.DireccionDtoKonverterKt;
import io.github.kingg22.api.vacunas.panama.web.dto.PacienteDto;
import io.github.kingg22.api.vacunas.panama.web.dto.ViewPacienteVacunaEnfermedadDto;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for {@link Paciente}. Extends {@link PersonaService} to inherit methods for directly modifying all details
 * related to a {@link Paciente}. This service allows for comprehensive management of patient information and integrates
 * with the underlying personal data structure.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PacienteService implements IPacienteService {
    private final PacienteRepository pacienteRepository;
    private final DireccionService direccionService;

    @Cacheable(cacheNames = "cache", key = "'view_vacuna_enfermedad'.concat(#idPaciente)")
    public List<ViewPacienteVacunaEnfermedadDto> getViewVacunaEnfermedad(UUID idPaciente) {
        return this.pacienteRepository.findAllFromViewVacunaEnfermedad(idPaciente);
    }

    public ApiContentResponse validateCreatePacienteUsuario(
            @org.jetbrains.annotations.NotNull PacienteDto pacienteDto) {
        var apiContentResponse = ApiResponseFactory.createContentResponse();
        if (pacienteDto.persona().usuario() == null) {
            apiContentResponse.addError(new DefaultApiError(
                    ApiResponseCode.MISSING_INFORMATION, "Esta función necesita el usuario para el paciente"));
        }
        if (pacienteDto.persona().usuario() != null
                && pacienteDto.persona().usuario().id() == null
                && pacienteDto.persona().usuario().roles().stream().anyMatch(rolDto -> rolDto.nombre() != null)) {
            apiContentResponse.addError(new DefaultApiError(
                    ApiResponseCode.NON_IDEMPOTENCE, "roles[]", "Utilice ID para el rol Paciente en esta función"));
        }
        if (pacienteDto.persona().usuario() != null
                && pacienteDto.persona().usuario().roles().stream()
                        .anyMatch(rolDto -> rolDto.id() != null
                                && !RolesEnum.getByPriority(rolDto.id()).equals(RolesEnum.PACIENTE))) {
            apiContentResponse.addError(new DefaultApiError(
                    ApiResponseCode.VALIDATION_FAILED,
                    "roles[]",
                    "Esta función es solo para pacientes, utilice otra operación"));
        }
        if (pacienteDto.persona().sexo() != null
                && pacienteDto.persona().sexo().toString().equalsIgnoreCase("X")) {
            apiContentResponse.addWarning(new DefaultApiError(
                    ApiResponseCode.DEPRECATION_WARNING,
                    "sexo",
                    "Pacientes no deben tener sexo no definido. Reglas de vacunación no se podrán aplicar"));
        }
        if (pacienteDto.persona().direccion() != null
                && pacienteDto.persona().direccion().id() == null) {
            apiContentResponse.addWarning(
                    new DefaultApiError(ApiResponseCode.NON_IDEMPOTENCE, "direccion", "Debe trabajar con ID"));
        }
        if (pacienteDto.persona().usuario() != null
                && pacienteDto.persona().usuario().createdAt() != null
                && pacienteDto.createdAt() != null
                && !pacienteDto
                        .createdAt()
                        .isEqual(pacienteDto.persona().usuario().createdAt())) {
            apiContentResponse.addError(new DefaultApiError(
                    ApiResponseCode.VALIDATION_FAILED,
                    "created_at",
                    "created_at de Paciente y Usuario deben ser las mismas o null"));
        }
        return apiContentResponse;
    }

    public List<ApiError> validateCreatePaciente(
            @org.jetbrains.annotations.NotNull @NotNull PacienteDto pacienteDto) {
        var failedList = new ArrayList<ApiError>();

        if ((pacienteDto.persona().nombre() == null
                        || pacienteDto.persona().nombre().isBlank())
                && (pacienteDto.persona().nombre2() == null
                        || pacienteDto.persona().nombre2().isBlank())) {
            failedList.add(new DefaultApiError(
                    ApiResponseCode.VALIDATION_FAILED, "nombre", "El nombre del paciente es obligatorio"));
        }

        if ((pacienteDto.persona().apellido1() == null
                        || pacienteDto.persona().apellido1().isBlank())
                && (pacienteDto.persona().apellido2() == null
                        || pacienteDto.persona().apellido2().isBlank())) {
            failedList.add(new DefaultApiError(
                    ApiResponseCode.VALIDATION_FAILED, "apellido", "El apellido del paciente es obligatorio"));
        }

        if ((pacienteDto.persona().cedula() == null
                        || pacienteDto.persona().cedula().isBlank())
                && (pacienteDto.persona().pasaporte() == null
                        || pacienteDto.persona().pasaporte().isBlank())
                && (pacienteDto.identificacionTemporal() == null
                        || pacienteDto.identificacionTemporal().isBlank())) {
            failedList.add(new DefaultApiError(
                    ApiResponseCode.VALIDATION_FAILED,
                    "identificacion",
                    "Una identificación personal del paciente es obligatoria"));
        }

        if (pacienteDto.persona().fechaNacimiento() == null) {
            failedList.add(new DefaultApiError(
                    ApiResponseCode.VALIDATION_FAILED,
                    "fechaNacimiento",
                    "La fecha de nacimiento del paciente es obligatoria"));
        }

        return failedList;
    }

    public List<ApiError> validatePacienteExist(
            @org.jetbrains.annotations.NotNull @NotNull PacienteDto pacienteDto) {
        var failedList = new ArrayList<ApiError>();

        if (pacienteDto.persona().cedula() != null) {
            pacienteDto = pacienteDto.changePersona(pacienteDto
                    .persona()
                    .changeCedulaOrID(
                            FormatterUtil.formatCedula(pacienteDto.persona().cedula())));

            if (this.pacienteRepository
                    .findByCedula(pacienteDto.persona().cedula())
                    .isPresent()) {
                log.debug(
                        "Existe un paciente con cédula a registrar: {}",
                        pacienteDto.persona().cedula());
                failedList.add(new DefaultApiError(
                        ApiResponseCode.VALIDATION_FAILED,
                        "cedula",
                        "Ya existe un paciente con la cédula proporcionada"));
            }
        }

        if (pacienteDto.persona().pasaporte() != null
                && !pacienteDto.persona().pasaporte().isBlank()
                && this.pacienteRepository
                        .findByPasaporte(pacienteDto.persona().pasaporte())
                        .isPresent()) {
            log.debug(
                    "Existe un paciente con pasaporte: {}",
                    pacienteDto.persona().pasaporte());
            failedList.add(new DefaultApiError(
                    ApiResponseCode.VALIDATION_FAILED,
                    "pasaporte",
                    "Ya existe un paciente con el pasaporte proporcionado"));
        }

        if (pacienteDto.identificacionTemporal() != null
                && !pacienteDto.identificacionTemporal().isBlank()
                && this.pacienteRepository
                        .findByIdentificacionTemporal(pacienteDto.identificacionTemporal())
                        .isPresent()) {
            log.debug("Existe un paciente con este identificador temporal: {}", pacienteDto.identificacionTemporal());
            failedList.add(new DefaultApiError(
                    ApiResponseCode.VALIDATION_FAILED,
                    "identificacionTemporal",
                    "Ya existe un paciente con la identificación temporal proporcionada"));
        }

        if (pacienteDto.persona().correo() != null
                && !pacienteDto.persona().correo().isBlank()
                && this.pacienteRepository
                        .findByCorreo(pacienteDto.persona().correo())
                        .isPresent()) {
            log.debug(
                    "Existe un paciente con este correo: {}",
                    pacienteDto.persona().correo());
            failedList.add(new DefaultApiError(
                    ApiResponseCode.VALIDATION_FAILED, "correo", "Ya existe un paciente con el correo proporcionado"));
        }

        if (pacienteDto.persona().telefono() != null
                && !pacienteDto.persona().telefono().isBlank()
                && this.pacienteRepository
                        .findByTelefono(pacienteDto.persona().telefono())
                        .isPresent()) {
            log.debug(
                    "Existe un paciente con este teléfono: {}",
                    pacienteDto.persona().telefono());
            failedList.add(new DefaultApiError(
                    ApiResponseCode.VALIDATION_FAILED,
                    "telefono",
                    "Ya existe un paciente con el teléfono proporcionado"));
        }

        return failedList;
    }

    @Transactional
    public ApiContentResponse createPaciente(@NotNull final PacienteDto pacienteDto) {
        var apiContentResponse = ApiResponseFactory.createContentResponse();
        apiContentResponse.addErrors(this.validatePacienteExist(pacienteDto));
        apiContentResponse.addErrors(this.validateCreatePaciente(pacienteDto));
        if (apiContentResponse.hasErrors()) {
            return apiContentResponse;
        }
        Direccion direccion;
        if (pacienteDto.persona().direccion() != null) {
            direccion = direccionService
                    .getDireccionByDto(pacienteDto.persona().direccion())
                    .orElseGet(() -> direccionService.createDireccion(
                            pacienteDto.persona().direccion()));
        } else {
            direccion = DireccionDtoKonverterKt.toDireccion(direccionService.getDireccionDtoDefault());
        }
        Paciente paciente = (Paciente) Paciente.builderPaciente()
                .identificacionTemporal(pacienteDto.identificacionTemporal())
                .createdAt(
                        pacienteDto.createdAt() != null ? pacienteDto.createdAt() : LocalDateTime.now(ZoneOffset.UTC))
                .nombre(pacienteDto.persona().nombre())
                .nombre2(pacienteDto.persona().nombre2())
                .apellido1(pacienteDto.persona().apellido1())
                .apellido2(pacienteDto.persona().apellido2())
                .fechaNacimiento(pacienteDto.persona().fechaNacimiento())
                .cedula(pacienteDto.persona().cedula())
                .pasaporte(pacienteDto.persona().pasaporte())
                .telefono(pacienteDto.persona().telefono())
                .correo(pacienteDto.persona().correo())
                .sexo(pacienteDto.persona().sexo())
                .direccion(direccion)
                .estado(pacienteDto.persona().estado())
                .disabled(pacienteDto.persona().disabled())
                .build();
        paciente = pacienteRepository.save(paciente);
        Hibernate.initialize(paciente.getDireccion());
        Hibernate.initialize(paciente.getDireccion().getDistrito());
        if (paciente.getDireccion().getDistrito() != null) {
            Hibernate.initialize(paciente.getDireccion().getDistrito().getProvincia());
        }
        Hibernate.initialize(paciente.getUsuario());
        var result = PacienteKonverterKt.toPacienteDto(paciente);
        apiContentResponse.addData("paciente", result);
        return apiContentResponse;
    }

    public Optional<Paciente> getPacienteByUserID(@NotNull UUID idUser) {
        return this.pacienteRepository.findByUsuario_Id(idUser);
    }

    public Optional<Paciente> getPacienteById(@NotNull UUID idPaciente) {
        return this.pacienteRepository.findById(idPaciente);
    }

    public PacienteDto getPacienteDtoById(@NotNull UUID idPaciente) {
        return PacienteKonverterKt.toPacienteDto(
                this.getPacienteById(idPaciente).orElseThrow());
    }
}
