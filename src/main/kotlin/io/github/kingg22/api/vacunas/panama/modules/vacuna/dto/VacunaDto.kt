package io.github.kingg22.api.vacunas.panama.modules.vacuna.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.modules.fabricante.dto.FabricanteDto
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

/** DTO for [io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.Vacuna] */
@JvmRecord
data class VacunaDto(
    val id: UUID? = null,

    @param:Size(max = 100) @field:Size(max = 100) @param:NotBlank @field:NotBlank val nombre: String,

    @param:JsonProperty(value = "edad_minima_dias")
    @field:JsonProperty(value = "edad_minima_dias")
    val edadMinimaDias: Short? = null,

    @param:JsonProperty(value = "dosis_maxima")
    @field:JsonProperty(value = "dosis_maxima")
    val dosisMaxima: NumDosisEnum? = null,

    @param:JsonProperty(value = "created_at")
    @field:JsonProperty(value = "created_at")
    @param:PastOrPresent
    @field:PastOrPresent
    val createdAt: LocalDateTime? = null,

    @param:JsonProperty(value = "updated_at", access = JsonProperty.Access.READ_ONLY)
    @field:JsonProperty(value = "updated_at", access = JsonProperty.Access.READ_ONLY)
    @param:PastOrPresent
    @field:PastOrPresent
    val updatedAt: LocalDateTime? = null,

    @param:Valid @field:Valid val fabricantes: Set<FabricanteDto> = emptySet(),
) : Serializable
