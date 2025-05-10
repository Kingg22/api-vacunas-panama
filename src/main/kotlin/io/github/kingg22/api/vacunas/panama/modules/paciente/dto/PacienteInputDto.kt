package io.github.kingg22.api.vacunas.panama.modules.paciente.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DireccionDto
import io.github.kingg22.api.vacunas.panama.modules.persona.dto.PersonaDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.UsuarioDto
import io.mcarle.konvert.api.KonvertTo
import io.quarkus.runtime.annotations.RegisterForReflection
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.util.UUID

@RegisterForReflection
@KonvertTo(PersonaDto::class)
@JvmRecord
data class PacienteInputDto(
    @all:Size(max = 100)
    val nombre: String? = null,

    @all:Size(max = 100)
    val nombre2: String? = null,

    val id: UUID? = null,

    @all:Size(max = 15)
    @all:Pattern(
        regexp = "^(PE|E|N|[23456789](?:AV|PI)?|1[0123]?(?:AV|PI)?)-(\\d{1,4})-(\\d{1,6})$",
        flags = [Pattern.Flag.CASE_INSENSITIVE],
        message = "El formato de la cédula no es válido",
    )
    val cedula: String? = null,

    @all:Size(max = 15)
    @all:Pattern(regexp = "^\\+\\d{1,14}$", message = "El formato del teléfono no es válido")
    val telefono: String? = null,

    @all:JsonProperty(value = "fecha_nacimiento")
    @all:PastOrPresent
    val fechaNacimiento: LocalDateTime? = null,

    @all:Size(max = 20)
    @all:Pattern(
        regexp = "^[A-Z0-9]{5,20}$",
        flags = [Pattern.Flag.CASE_INSENSITIVE],
        message = "El formato del pasaporte no es válido",
    )
    val pasaporte: String? = null,

    val edad: Short? = null,

    @all:Size(max = 1)
    val sexo: String? = null,

    @all:Size(max = 50)
    val estado: String? = null,

    @all:Size(max = 100)
    val apellido1: String? = null,

    @all:Size(max = 100)
    val apellido2: String? = null,

    @all:Size(max = 254) @all:Email
    val correo: String? = null,

    @all:Valid
    val direccion: DireccionDto = DireccionDto(),

    @all:Valid
    val usuario: UsuarioDto? = null,

    @all:JsonProperty(value = "created_at")
    @all:PastOrPresent
    val createdAt: LocalDateTime = LocalDateTime.now(UTC),

    @all:JsonProperty(value = "updated_at")
    @all:PastOrPresent
    val updatedAt: LocalDateTime? = null,

    @all:JsonProperty(value = "identificacion_temporal")
    @all:Size(max = 255)
    @all:Pattern(
        regexp =
        "^(RN(\\\\d{1,2}?)?)-(PE|E|N|[23456789](?:AV|PI)?|1[0123]?(?:AV|PI)?)-(\\\\d{1,4})-(\\\\d{1,6})$|^NI-.+$",
        flags = [Pattern.Flag.CASE_INSENSITIVE, Pattern.Flag.MULTILINE],
        message = "El formato de id temporal no es válido",
    )
    val identificacionTemporal: String? = null,
) : Serializable {
    fun toPacienteDto() = PacienteDto(
        persona = this.toPersonaDto(),
        identificacionTemporal = this.identificacionTemporal,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
}
