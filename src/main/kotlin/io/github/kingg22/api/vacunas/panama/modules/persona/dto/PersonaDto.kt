package io.github.kingg22.api.vacunas.panama.modules.persona.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DireccionDto
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.UsuarioDto
import io.mcarle.konvert.api.KonvertTo
import io.mcarle.konvert.api.Mapping
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

/**
 * DTO for [io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona]
 *
 * _Warning_:
 * - [Persona.direccion] is required, but in DTO it's nullable.
 * Default value is [DireccionDto] with [DireccionDto.DEFAULT_DIRECCION].
 * - [Persona.estado] is required, but in DTO it's nullable.
 * The Default value is [PersonaDto.DEFAULT_ESTADO].
 */
@JvmRecord
@KonvertTo(Persona::class, mappings = [Mapping(target = "estado", expression = "estado ?: PersonaDto.DEFAULT_ESTADO")])
data class PersonaDto(
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

    @field:Size(max = 100) @param:Size(max = 100) val nombre: String? = null,

    @field:Size(max = 100) @param:Size(max = 100) val nombre2: String? = null,

    @field:Size(max = 100) @param:Size(max = 100) val apellido1: String? = null,

    @field:Size(max = 100) @param:Size(max = 100) val apellido2: String? = null,

    @field:Size(max = 254) @param:Size(max = 254) @field:Email @param:Email val correo: String? = null,

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

    @field:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @param:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val edad: Short? = null,

    @field:Size(max = 1)
    @param:Size(max = 1)
    val sexo: String? = null,

    @field:Size(max = 50) @param:Size(max = 50) val estado: String? = null,

    val disabled: Boolean = false,

    @field:Valid @param:Valid val direccion: DireccionDto = DireccionDto(),

    @field:Valid @param:Valid val usuario: UsuarioDto? = null,
) : Serializable {
    companion object {
        const val DEFAULT_ESTADO = "NO_VALIDADO"
    }
}
