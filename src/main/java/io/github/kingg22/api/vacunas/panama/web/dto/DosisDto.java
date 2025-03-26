package io.github.kingg22.api.vacunas.panama.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.kingg22.api.vacunas.panama.persistence.entity.Dosis;
import io.github.kingg22.api.vacunas.panama.util.NumDosisEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link Dosis}
 */
public record DosisDto(
        @NotNull UUID id,
        @NotNull @Valid PacienteDto paciente,
        @NotNull @JsonProperty(value = "fecha_aplicacion") LocalDateTime fechaAplicacion,
        @NotNull @JsonProperty(value = "numero_dosis") NumDosisEnum numeroDosis,
        @NotNull @Valid VacunaDto vacuna,
        @NotNull @Valid SedeDto sede,
        @Valid DoctorDto doctor,
        @Size(max = 50) String lote,
        @NotNull @PastOrPresent @JsonProperty(value = "created_at") LocalDateTime createdAt,
        @PastOrPresent @JsonProperty(value = "updated_at") LocalDateTime updatedAt)
        implements Serializable {}
