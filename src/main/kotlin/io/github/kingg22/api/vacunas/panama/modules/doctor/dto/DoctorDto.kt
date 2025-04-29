package io.github.kingg22.api.vacunas.panama.modules.doctor.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.modules.doctor.domain.DoctorModel
import io.github.kingg22.api.vacunas.panama.modules.doctor.entity.Doctor
import io.github.kingg22.api.vacunas.panama.modules.persona.dto.PersonaDto
import io.mcarle.konvert.api.KonvertTo
import io.mcarle.konvert.api.Mapping
import jakarta.validation.Valid
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC

/**
 * DTO for [io.github.kingg22.api.vacunas.panama.modules.doctor.entity.Doctor]
 *
 * _Warning_:
 * - [Doctor.idoneidad] is required, but in DTO it's nullable.
 * Set is an empty string if it's null.
*/
@JvmRecord
@KonvertTo(
    Doctor::class,
    mappings = [
        Mapping("id", ignore = true),
        Mapping("sede", ignore = true),
        Mapping(target = "idoneidad", expression = "idoneidad ?: \"\""),
    ],
)
@KonvertTo(DoctorModel::class)
data class DoctorDto(
    @field:Valid @param:Valid val persona: PersonaDto,

    @field:Size(max = 20)
    @param:Size(max = 20)
    val idoneidad: String? = null,

    @field:Size(max = 100)
    @param:Size(max = 100)
    val categoria: String? = null,

    @field:JsonProperty(value = "created_at")
    @param:JsonProperty(value = "created_at")
    @field:PastOrPresent
    @param:PastOrPresent
    val createdAt: LocalDateTime = LocalDateTime.now(UTC),

    @field:JsonProperty(value = "updated_at", access = JsonProperty.Access.READ_ONLY)
    @param:JsonProperty(value = "updated_at", access = JsonProperty.Access.READ_ONLY)
    @field:PastOrPresent
    @param:PastOrPresent
    val updatedAt: LocalDateTime? = null,
) : Serializable
