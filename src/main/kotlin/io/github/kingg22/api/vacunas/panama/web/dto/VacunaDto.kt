package io.github.kingg22.api.vacunas.panama.web.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.util.NumDosisEnum
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

/** DTO for [io.github.kingg22.api.vacunas.panama.persistence.entity.Vacuna] */
@JvmRecord
data class VacunaDto @JvmOverloads constructor(
    val id: UUID? = null,
    @Size(max = 100) @NotBlank val nombre: String,
    @JsonProperty(value = "edad_minima_dias") val edadMinimaDias: Short? = null,
    @JsonProperty(value = "intervalo_dosis_dias") val intervaloDosisDias: Int? = null,
    @JsonProperty(value = "dosis_maxima") val dosisMaxima: NumDosisEnum? = null,
    @JsonProperty(value = "created_at") @PastOrPresent val createdAt: LocalDateTime? = null,
    @JsonProperty(value = "updated_at") @PastOrPresent val updatedAt: LocalDateTime? = null,
    @Valid val fabricantes: Set<FabricanteDto> = emptySet(),
) : Serializable
