package io.github.kingg22.api.vacunas.panama.web.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime

/** DTO for [io.github.kingg22.api.vacunas.panama.persistence.entity.Paciente]  */
class PacienteDto :
    PersonaDto(),
    Serializable {

    @JsonProperty(value = "identificacion_temporal")
    @Size(max = 255)
    @Pattern(
        regexp =
        "^(RN(\\\\d{1,2}?)?)-(PE|E|N|[23456789](?:AV|PI)?|1[0123]?(?:AV|PI)?)-(\\\\d{1,4})-(\\\\d{1,6})$|^NI-.+$",
        flags = [Pattern.Flag.CASE_INSENSITIVE, Pattern.Flag.MULTILINE],
        message = "El formato de id temporal no es válido",
    )
    val identificacionTemporal: String? = null

    @JsonProperty(value = "created_at")
    @PastOrPresent
    val createdAt: LocalDateTime? = null

    @JsonProperty(value = "updated_at")
    @PastOrPresent
    val updatedAt: LocalDateTime? = null
}
