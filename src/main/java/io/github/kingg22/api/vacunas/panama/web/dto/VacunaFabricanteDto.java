package io.github.kingg22.api.vacunas.panama.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.UUID;

public record VacunaFabricanteDto(
        UUID id_vacuna,
        @NotNull @Size(max = 100) @NotBlank @JsonProperty(value = "nombre_vacuna") String nombreVacuna,
        @JsonProperty(value = "id_fabricante") UUID idFabricante,
        @NotNull @Size(max = 100) @JsonProperty(value = "nombre_fabricante") String nombreFabricante)
        implements Serializable {}
