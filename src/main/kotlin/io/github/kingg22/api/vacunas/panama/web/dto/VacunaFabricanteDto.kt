package io.github.kingg22.api.vacunas.panama.web.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.util.UUID

@JvmRecord
data class VacunaFabricanteDto(
    @JsonProperty(value = "id_vacuna") val idVacuna: UUID,
    @Size(max = 100) @JsonProperty(value = "nombre_vacuna") val nombreVacuna: @NotBlank String,
    @JsonProperty(value = "id_fabricante") val idFabricante: UUID,
    @Size(max = 100) @JsonProperty(value = "nombre_fabricante") val nombreFabricante: String,
) : Serializable
