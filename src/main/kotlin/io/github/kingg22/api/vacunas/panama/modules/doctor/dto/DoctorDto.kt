package io.github.kingg22.api.vacunas.panama.modules.doctor.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.modules.doctor.domain.DoctorModel
import io.github.kingg22.api.vacunas.panama.modules.doctor.entity.Doctor
import io.github.kingg22.api.vacunas.panama.modules.persona.dto.PersonaDto
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo
import io.mcarle.konvert.api.Mapping
import io.quarkus.runtime.annotations.RegisterForReflection
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
@RegisterForReflection
@KonvertTo(
    Doctor::class,
    mappings = [
        Mapping("id", ignore = true),
        Mapping("sede", ignore = true),
        Mapping(target = "idoneidad", expression = "idoneidad ?: \"\""),
    ],
)
@KonvertTo(DoctorModel::class)
@KonvertFrom(Doctor::class)
@JvmRecord
data class DoctorDto(
    @all:Valid
    val persona: PersonaDto,

    @all:Size(max = 20)
    val idoneidad: String? = null,

    @all:Size(max = 100)
    val categoria: String? = null,

    @all:JsonProperty(value = "created_at")
    @all:PastOrPresent
    val createdAt: LocalDateTime = LocalDateTime.now(UTC),

    @all:JsonProperty(value = "updated_at")
    @all:PastOrPresent
    val updatedAt: LocalDateTime? = null,
) : Serializable {
    companion object
}
