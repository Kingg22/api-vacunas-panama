package com.kingg.api_vacunas_panama.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kingg.api_vacunas_panama.util.NumDosisEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for insert new {@link com.kingg.api_vacunas_panama.persistence.entity.Dosis}
 */
public record InsertDosisDto(@NotNull
                             @JsonProperty(value = "paciente_id")
                             UUID pacienteId,
                             @PastOrPresent
                             @JsonProperty(value = "fecha_aplicacion")
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                             LocalDateTime fechaAplicacion,
                             @NotNull
                             @JsonProperty(value = "numero_dosis")
                             NumDosisEnum numeroDosis,
                             @NotNull
                             @JsonProperty(value = "vacuna_id")
                             UUID vacunaId,
                             @NotNull
                             @JsonProperty(value = "sede_id")
                             UUID sedeId,
                             @Size(max = 50)
                             String lote,
                             @JsonProperty(value = "doctor_id")
                             UUID doctorId) implements Serializable {
}
