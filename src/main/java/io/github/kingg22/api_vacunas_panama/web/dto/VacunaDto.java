package io.github.kingg22.api_vacunas_panama.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.kingg22.api_vacunas_panama.persistence.entity.Vacuna;
import io.github.kingg22.api_vacunas_panama.util.NumDosisEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for {@link Vacuna}
 */
public record VacunaDto(UUID id,
                        @NotNull @Size(max = 100)
                        @NotBlank String nombre,
                        @JsonProperty(value = "edad_minima_dias")
                        Short edadMinimaDias,
                        @JsonProperty(value = "intervalo_dosis_dias")
                        Float intervaloDosisDias,
                        @JsonProperty(value = "dosis_maxima")
                        NumDosisEnum dosisMaxima,
                        @PastOrPresent @JsonProperty(value = "created_at") LocalDateTime createdAt,
                        @PastOrPresent @JsonProperty(value = "updated_at") LocalDateTime updatedAt,
                        @Valid Set<FabricanteDto> fabricantes) implements Serializable {
}
