package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.modules.direccion.extensions.DosisExtKt;
import io.github.kingg22.api.vacunas.panama.modules.doctor.entity.Doctor;
import io.github.kingg22.api.vacunas.panama.modules.doctor.service.IDoctorService;
import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente;
import io.github.kingg22.api.vacunas.panama.modules.paciente.service.IPacienteService;
import io.github.kingg22.api.vacunas.panama.modules.sede.entity.Sede;
import io.github.kingg22.api.vacunas.panama.modules.sede.service.ISedeService;
import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.DosisDto;
import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.InsertDosisDto;
import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.VacunaFabricanteDto;
import io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.Dosis;
import io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.DosisKonverterKt;
import io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.Vacuna;
import io.github.kingg22.api.vacunas.panama.modules.vacuna.repository.DosisRepository;
import io.github.kingg22.api.vacunas.panama.modules.vacuna.repository.VacunaRepository;
import io.github.kingg22.api.vacunas.panama.modules.vacuna.service.IVacunaService;
import io.github.kingg22.api.vacunas.panama.response.ApiContentResponse;
import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode;
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory;
import io.github.kingg22.api.vacunas.panama.response.DefaultApiError;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/** Service for {@link Vacuna} and {@link Dosis} */
@Slf4j
@Service
@RequiredArgsConstructor
public class VacunaService implements IVacunaService {
    private final VacunaRepository vacunaRepository;
    private final DosisRepository dosisRepository;
    private final IPacienteService pacienteService;
    private final ISedeService sedeService;
    private final IDoctorService doctorService;

    @org.jetbrains.annotations.NotNull
    @Cacheable(cacheNames = "huge", key = "'vacunas'")
    public List<VacunaFabricanteDto> getVacunasFabricante() {
        return vacunaRepository.findAllIdAndNombreAndFabricante();
    }

    @org.jetbrains.annotations.NotNull
    @Transactional
    public ApiContentResponse createDosis(@org.jetbrains.annotations.NotNull @NotNull InsertDosisDto insertDosisDto) {
        var apiContentResponse = ApiResponseFactory.createContentResponse();
        log.debug(insertDosisDto.toString());
        Paciente paciente = validatePacienteExist(insertDosisDto.pacienteId());
        Vacuna vacuna = validateVacunaExist(insertDosisDto.vacunaId());
        Sede sede = validateSedeExist(insertDosisDto.sedeId());
        Doctor doctor = validateDoctorExist(insertDosisDto.doctorId());
        log.debug("Paciente ID: {}", paciente.getId());
        log.debug("Vacuna ID: {}", vacuna.getId());
        log.debug("Sede ID: {}", sede.getId());
        this.dosisRepository
                .findTopByPacienteAndVacunaOrderByCreatedAtDesc(paciente, vacuna)
                .ifPresentOrElse(
                        ultimaDosis -> {
                            log.debug("Dosis encontrada ID: {}", ultimaDosis.getId());
                            if (!ultimaDosis.getNumeroDosis().isValidNew(insertDosisDto.numeroDosis())) {
                                apiContentResponse.addError(new DefaultApiError(
                                        ApiResponseCode.VALIDATION_FAILED,
                                        String.format(
                                                "La dosis %s no es válida. Último número de dosis %s",
                                                insertDosisDto.numeroDosis(), ultimaDosis.getNumeroDosis())));
                            }
                            log.debug("Nueva dosis cumple las reglas de secuencia en número de dosis");
                        },
                        () -> log.debug("El paciente no tiene dosis previas"));

        Dosis dosis = Dosis.builder()
                .paciente(paciente)
                .fechaAplicacion(
                        insertDosisDto.fechaAplicacion() != null
                                ? insertDosisDto.fechaAplicacion()
                                : LocalDateTime.now(ZoneOffset.UTC))
                .numeroDosis(insertDosisDto.numeroDosis())
                .vacuna(vacuna)
                .sede(sede)
                .lote(insertDosisDto.lote())
                .doctor(doctor)
                .createdAt(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        dosis = dosisRepository.save(dosis);
        dosis = dosisRepository.findById(dosis.getId()).orElseThrow();
        log.debug("Nueva dosis. ID: {}", dosis.getId());
        DosisDto dosisDto = DosisKonverterKt.toDosisDto(dosis);
        apiContentResponse.addData("dosis", dosisDto);
        return apiContentResponse;
    }

    @org.jetbrains.annotations.NotNull
    public List<DosisDto> getDosisByIdPacienteIdVacuna(
            @org.jetbrains.annotations.NotNull UUID idPaciente, @org.jetbrains.annotations.NotNull UUID idVacuna) {
        return DosisExtKt.toListDosisDto(
                dosisRepository.findAllByPaciente_IdAndVacuna_IdOrderByCreatedAtDesc(idPaciente, idVacuna));
    }

    Paciente validatePacienteExist(UUID pacienteId) {
        return this.pacienteService
                .getPacienteById(pacienteId)
                .orElseThrow(() -> new NoSuchElementException("Paciente no encontrado"));
    }

    Vacuna validateVacunaExist(UUID vacunaId) {
        return this.vacunaRepository
                .findById(vacunaId)
                .orElseThrow(() -> new NoSuchElementException("Vacuna no encontrada"));
    }

    Sede validateSedeExist(UUID sedeId) {
        return this.sedeService.getSedeById(sedeId).orElseThrow(() -> new NoSuchElementException("Sede no encontrado"));
    }

    Doctor validateDoctorExist(UUID doctorId) {
        if (doctorId != null) {
            return this.doctorService
                    .getDoctorById(doctorId)
                    .orElseThrow(() -> new NoSuchElementException("Doctor no encontrado"));
        }
        return null;
    }
}
