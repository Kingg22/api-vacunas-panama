package io.github.kingg22.api.vacunas.panama.modules.paciente.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DireccionDto
import io.github.kingg22.api.vacunas.panama.modules.persona.dto.PersonaDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.UsuarioDto
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.util.UUID

@JvmRecord
data class PacienteInputDto(
    @field:Size(max = 100) @param:Size(max = 100) val nombre: String? = null,

    @field:Size(max = 100) @param:Size(max = 100) val nombre2: String? = null,

    val id: UUID? = null,

    @field:Size(max = 15)
    @param:Size(max = 15)
    @field:Pattern(
        regexp = "^(PE|E|N|[23456789](?:AV|PI)?|1[0123]?(?:AV|PI)?)-(\\d{1,4})-(\\d{1,6})$",
        flags = [Pattern.Flag.CASE_INSENSITIVE],
        message = "El formato de la cédula no es válido",
    )
    @param:Pattern(
        regexp = "^(PE|E|N|[23456789](?:AV|PI)?|1[0123]?(?:AV|PI)?)-(\\d{1,4})-(\\d{1,6})$",
        flags = [Pattern.Flag.CASE_INSENSITIVE],
        message = "El formato de la cédula no es válido",
    )
    val cedula: String? = null,

    @field:Size(max = 15)
    @field:Pattern(regexp = "^\\+\\d{1,14}$", message = "El formato del teléfono no es válido")
    @param:Size(max = 15)
    @param:Pattern(regexp = "^\\+\\d{1,14}$", message = "El formato del teléfono no es válido")
    val telefono: String? = null,

    @field:JsonProperty(value = "fecha_nacimiento")
    @param:JsonProperty(value = "fecha_nacimiento")
    @field:PastOrPresent
    @param:PastOrPresent
    val fechaNacimiento: LocalDateTime? = null,

    @field:Size(max = 20)
    @field:Pattern(
        regexp = "^[A-Z0-9]{5,20}$",
        flags = [Pattern.Flag.CASE_INSENSITIVE],
        message = "El formato del pasaporte no es válido",
    )
    @param:Size(max = 20)
    @param:Pattern(
        regexp = "^[A-Z0-9]{5,20}$",
        flags = [Pattern.Flag.CASE_INSENSITIVE],
        message = "El formato del pasaporte no es válido",
    )
    val pasaporte: String? = null,

    @field:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @param:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val edad: Short? = null,

    @field:Size(max = 1)
    @param:Size(max = 1)
    val sexo: String? = null,

    @field:Size(max = 50) @param:Size(max = 50) val estado: String? = null,

    @field:Size(max = 100) @param:Size(max = 100) val apellido1: String? = null,

    @field:Size(max = 100) @param:Size(max = 100) val apellido2: String? = null,

    @field:Size(max = 254) @param:Size(max = 254) @field:Email @param:Email val correo: String? = null,

    @field:Valid @param:Valid val direccion: DireccionDto = DireccionDto(),

    @field:Valid @param:Valid val usuario: UsuarioDto? = null,

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

    @field:JsonProperty(value = "identificacion_temporal")
    @param:JsonProperty(value = "identificacion_temporal")
    @field:Size(max = 255)
    @param:Size(max = 255)
    @field:Pattern(
        regexp =
        "^(RN(\\\\d{1,2}?)?)-(PE|E|N|[23456789](?:AV|PI)?|1[0123]?(?:AV|PI)?)-(\\\\d{1,4})-(\\\\d{1,6})$|^NI-.+$",
        flags = [Pattern.Flag.CASE_INSENSITIVE, Pattern.Flag.MULTILINE],
        message = "El formato de id temporal no es válido",
    )
    @param:Pattern(
        regexp =
        "^(RN(\\\\d{1,2}?)?)-(PE|E|N|[23456789](?:AV|PI)?|1[0123]?(?:AV|PI)?)-(\\\\d{1,4})-(\\\\d{1,6})$|^NI-.+$",
        flags = [Pattern.Flag.CASE_INSENSITIVE, Pattern.Flag.MULTILINE],
        message = "El formato de id temporal no es válido",
    )
    val identificacionTemporal: String? = null,

) : Serializable {
    fun toPacienteDto() = PacienteDto(
        persona = PersonaDto(
            id = id,
            cedula = cedula,
            pasaporte = pasaporte,
            nombre = nombre,
            nombre2 = nombre2,
            apellido1 = apellido1,
            apellido2 = apellido2,
            correo = correo,
            telefono = telefono,
            fechaNacimiento = fechaNacimiento,
            edad = edad,
            sexo = sexo,
            estado = estado,
            direccion = direccion,
            usuario = usuario,
        ),
        identificacionTemporal = identificacionTemporal,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
