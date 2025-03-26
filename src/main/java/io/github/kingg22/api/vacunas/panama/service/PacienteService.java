package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Direccion;
import io.github.kingg22.api.vacunas.panama.persistence.entity.Paciente;
import io.github.kingg22.api.vacunas.panama.persistence.repository.PacienteRepository;
import io.github.kingg22.api.vacunas.panama.response.ApiContentResponse;
import io.github.kingg22.api.vacunas.panama.response.ApiFailed;
import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode;
import io.github.kingg22.api.vacunas.panama.util.FormatterUtil;
import io.github.kingg22.api.vacunas.panama.util.RolesEnum;
import io.github.kingg22.api.vacunas.panama.util.mapper.DireccionMapper;
import io.github.kingg22.api.vacunas.panama.util.mapper.PacienteMapper;
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
 * Service for {@link Paciente}.
 * Extends {@link PersonaService} to inherit methods for directly modifying all details related to a {@link Paciente}.
 * This service allows for comprehensive management of patient information and integrates with
 * the underlying personal data structure.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PacienteService implements IPacienteService {
    private final PacienteMapper mapper;
    private final DireccionMapper direccionMapper;
    private final PacienteRepository pacienteRepository;
    private final DireccionService direccionService;

    @Cacheable(cacheNames = "cache", key = "'view_vacuna_enfermedad'.concat(#idPaciente)")
    public List<ViewPacienteVacunaEnfermedadDto> getViewVacunaEnfermedad(UUID idPaciente) {
        List<ViewPacienteVacunaEnfermedadDto> view =
                this.pacienteRepository.findAllFromViewVacunaEnfermedad(idPaciente);
        if (view.isEmpty()) {
            return List.of();
        } else {
            return view;
        }
    }

    public ApiContentResponse validateCreatePacienteUsuario(PacienteDto pacienteDto) {
        ApiContentResponse apiContentResponse = new ApiContentResponse();
        if (pacienteDto.getUsuario() == null) {
            apiContentResponse.addError(
                    ApiResponseCode.MISSING_INFORMATION, "Esta función necesita el usuario para el paciente");
        }
        if (pacienteDto.getUsuario() != null
                && pacienteDto.getUsuario().id() == null
                && pacienteDto.getUsuario().roles().stream().anyMatch(rolDto -> rolDto.nombre() != null)) {
            apiContentResponse.addError(
                    ApiResponseCode.NON_IDEMPOTENCE, "roles[]", "Utilice ID para el rol Paciente en esta función");
        }
        if (pacienteDto.getUsuario() != null
                && pacienteDto.getUsuario().roles().stream()
                        .anyMatch(rolDto -> rolDto.id() != null
                                && !RolesEnum.getByPriority(rolDto.id()).equals(RolesEnum.PACIENTE))) {
            apiContentResponse.addError(
                    ApiResponseCode.VALIDATION_FAILED,
                    "roles[]",
                    "Esta función es solo para pacientes, utilice otra operación");
        }
        if (pacienteDto.getSexo().toString().equalsIgnoreCase("X")) {
            apiContentResponse.addWarning(
                    ApiResponseCode.DEPRECATION_WARNING,
                    "sexo",
                    "Pacientes no deben tener sexo no definido. Reglas de vacunación no se podrán aplicar");
        }
        if (pacienteDto.getDireccion() != null && pacienteDto.getDireccion().id() == null) {
            apiContentResponse.addWarning(ApiResponseCode.NON_IDEMPOTENCE, "direccion", "Debe trabajar con ID");
        }
        if (pacienteDto.getUsuario() != null
                && pacienteDto.getUsuario().createdAt() != null
                && pacienteDto.getCreatedAt() != null
                && !pacienteDto.getCreatedAt().isEqual(pacienteDto.getUsuario().createdAt())) {
            apiContentResponse.addError(
                    ApiResponseCode.VALIDATION_FAILED,
                    "created_at",
                    "created_at de Paciente y Usuario deben ser las mismas o null");
        }
        return apiContentResponse;
    }

    public List<ApiFailed> validateCreatePaciente(@NotNull PacienteDto pacienteDto) {
        List<ApiFailed> failedList = new ArrayList<>();

        if ((pacienteDto.getNombre() == null || pacienteDto.getNombre().isBlank())
                && (pacienteDto.getNombre2() == null || pacienteDto.getNombre2().isBlank())) {
            failedList.add(new ApiFailed(
                    ApiResponseCode.VALIDATION_FAILED, "nombre", "El nombre del paciente es obligatorio"));
        }

        if ((pacienteDto.getApellido1() == null || pacienteDto.getApellido1().isBlank())
                && (pacienteDto.getApellido2() == null
                        || pacienteDto.getApellido2().isBlank())) {
            failedList.add(new ApiFailed(
                    ApiResponseCode.VALIDATION_FAILED, "apellido", "El apellido del paciente es obligatorio"));
        }

        if ((pacienteDto.getCedula() == null || pacienteDto.getCedula().isBlank())
                && (pacienteDto.getPasaporte() == null
                        || pacienteDto.getPasaporte().isBlank())
                && (pacienteDto.getIdentificacionTemporal() == null
                        || pacienteDto.getIdentificacionTemporal().isBlank())) {
            failedList.add(new ApiFailed(
                    ApiResponseCode.VALIDATION_FAILED,
                    "identificacion",
                    "Una identificación personal del paciente es obligatoria"));
        }

        if (pacienteDto.getFechaNacimiento() == null) {
            failedList.add(new ApiFailed(
                    ApiResponseCode.VALIDATION_FAILED,
                    "fechaNacimiento",
                    "La fecha de nacimiento del paciente es obligatoria"));
        }

        return failedList;
    }

    public List<ApiFailed> validatePacienteExist(@NotNull PacienteDto pacienteDto) {
        List<ApiFailed> failedList = new ArrayList<>();

        if (pacienteDto.getCedula() != null) {
            pacienteDto.setCedula(FormatterUtil.formatCedula(pacienteDto.getCedula()));
            if (this.pacienteRepository.findByCedula(pacienteDto.getCedula()).isPresent()) {
                log.debug("Existe un paciente con cédula a registrar: {}", pacienteDto.getCedula());
                failedList.add(new ApiFailed(
                        ApiResponseCode.VALIDATION_FAILED,
                        "cedula",
                        "Ya existe un paciente con la cédula proporcionada"));
            }
        }

        if (pacienteDto.getPasaporte() != null
                && !pacienteDto.getPasaporte().isBlank()
                && this.pacienteRepository
                        .findByPasaporte(pacienteDto.getPasaporte())
                        .isPresent()) {
            log.debug("Existe un paciente con pasaporte: {}", pacienteDto.getPasaporte());
            failedList.add(new ApiFailed(
                    ApiResponseCode.VALIDATION_FAILED,
                    "pasaporte",
                    "Ya existe un paciente con el pasaporte proporcionado"));
        }

        if (pacienteDto.getIdentificacionTemporal() != null
                && !pacienteDto.getIdentificacionTemporal().isBlank()
                && this.pacienteRepository
                        .findByIdentificacionTemporal(pacienteDto.getIdentificacionTemporal())
                        .isPresent()) {
            log.debug(
                    "Existe un paciente con este identificador temporal: {}", pacienteDto.getIdentificacionTemporal());
            failedList.add(new ApiFailed(
                    ApiResponseCode.VALIDATION_FAILED,
                    "identificacionTemporal",
                    "Ya existe un paciente con la identificación temporal proporcionada"));
        }

        if (pacienteDto.getCorreo() != null
                && !pacienteDto.getCorreo().isBlank()
                && this.pacienteRepository.findByCorreo(pacienteDto.getCorreo()).isPresent()) {
            log.debug("Existe un paciente con este correo: {}", pacienteDto.getCorreo());
            failedList.add(new ApiFailed(
                    ApiResponseCode.VALIDATION_FAILED, "correo", "Ya existe un paciente con el correo proporcionado"));
        }

        if (pacienteDto.getTelefono() != null
                && !pacienteDto.getTelefono().isBlank()
                && this.pacienteRepository
                        .findByTelefono(pacienteDto.getTelefono())
                        .isPresent()) {
            log.debug("Existe un paciente con este teléfono: {}", pacienteDto.getTelefono());
            failedList.add(new ApiFailed(
                    ApiResponseCode.VALIDATION_FAILED,
                    "telefono",
                    "Ya existe un paciente con el teléfono proporcionado"));
        }

        return failedList;
    }

    @Transactional
    public ApiContentResponse createPaciente(@NotNull final PacienteDto pacienteDto) {
        ApiContentResponse apiContentResponse = new ApiContentResponse();
        apiContentResponse.addErrors(this.validatePacienteExist(pacienteDto));
        apiContentResponse.addErrors(this.validateCreatePaciente(pacienteDto));
        if (apiContentResponse.hasErrors()) {
            return apiContentResponse;
        }
        Direccion direccion;
        if (pacienteDto.getDireccion() != null) {
            direccion = direccionService
                    .getDireccionByDto(pacienteDto.getDireccion())
                    .orElseGet(() -> direccionService.createDireccion(pacienteDto.getDireccion()));
        } else {
            direccion = direccionMapper.direccionDtoToEntity(direccionService.getDireccionDtoDefault());
        }
        Paciente paciente = (Paciente) Paciente.builderPaciente()
                .identificacionTemporal(pacienteDto.getIdentificacionTemporal())
                .nombre(pacienteDto.getNombre())
                .nombre2(pacienteDto.getNombre2())
                .apellido1(pacienteDto.getApellido1())
                .apellido2(pacienteDto.getApellido2())
                .fechaNacimiento(pacienteDto.getFechaNacimiento())
                .cedula(pacienteDto.getCedula())
                .pasaporte(pacienteDto.getPasaporte())
                .telefono(pacienteDto.getTelefono())
                .correo(pacienteDto.getCorreo())
                .sexo(pacienteDto.getSexo())
                .direccion(direccion)
                .estado(pacienteDto.getEstado())
                .disabled(pacienteDto.getDisabled())
                .createdAt(
                        pacienteDto.getCreatedAt() != null
                                ? pacienteDto.getCreatedAt()
                                : LocalDateTime.now(ZoneOffset.UTC))
                .build();
        paciente = pacienteRepository.save(paciente);
        Hibernate.initialize(paciente.getDireccion());
        Hibernate.initialize(paciente.getDireccion().getDistrito());
        Hibernate.initialize(paciente.getDireccion().getDistrito().getProvincia());
        Hibernate.initialize(paciente.getUsuario());
        var result = mapper.toDto(paciente);
        apiContentResponse.addData("paciente", result);
        return apiContentResponse;
    }

    Optional<Paciente> getPacienteByUserID(@NotNull UUID idUser) {
        return this.pacienteRepository.findByUsuario_Id(idUser);
    }

    Optional<Paciente> getPacienteById(@NotNull UUID idPaciente) {
        return this.pacienteRepository.findById(idPaciente);
    }

    public PacienteDto getPacienteDtoById(@NotNull UUID idPaciente) {
        return mapper.toDto(this.getPacienteById(idPaciente).orElseThrow());
    }

    public List<ViewPacienteVacunaEnfermedadDto> getVacunaPaciente(UUID idPaciente, UUID idVacuna) {
        List<ViewPacienteVacunaEnfermedadDto> view =
                this.pacienteRepository.findAllFromViewVacunaEnfermedad(idPaciente, idVacuna);
        if (view.isEmpty()) {
            return List.of();
        } else {
            return view;
        }
    }
}
