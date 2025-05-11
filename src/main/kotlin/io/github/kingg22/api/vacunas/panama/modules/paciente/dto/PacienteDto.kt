package io.github.kingg22.api.vacunas.panama.modules.paciente.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.modules.paciente.domain.PacienteModel
import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente
import io.github.kingg22.api.vacunas.panama.modules.persona.dto.PersonaDto
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo
import io.mcarle.konvert.api.Mapping
import io.quarkus.runtime.annotations.RegisterForReflection
import jakarta.validation.Valid
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC

/** DTO for [io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente] */
@RegisterForReflection
@KonvertTo(Paciente::class, [Mapping("id", ignore = true)])
@KonvertTo(PacienteModel::class)
@KonvertFrom(Paciente::class)
@JvmRecord
data class PacienteDto(
    @all:Valid
    val persona: PersonaDto,

    @all:JsonProperty(value = "identificacion_temporal")
    @all:Size(max = 255)
    @all:Pattern(
        regexp =
        "^(RN(\\\\d{1,2}?)?)-(PE|E|N|[23456789](?:AV|PI)?|1[0123]?(?:AV|PI)?)-(\\\\d{1,4})-(\\\\d{1,6})$|^NI-.+$",
        flags = [Pattern.Flag.CASE_INSENSITIVE, Pattern.Flag.MULTILINE],
        message = "El formato de id temporal no es válido",
    )
    val identificacionTemporal: String? = null,

    @all:JsonProperty(value = "created_at")
    @all:PastOrPresent
    val createdAt: LocalDateTime = LocalDateTime.now(UTC),

    @all:JsonProperty(value = "updated_at")
    @all:PastOrPresent
    val updatedAt: LocalDateTime? = null,
) : Serializable {
    companion object
}
