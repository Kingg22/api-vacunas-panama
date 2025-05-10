package io.github.kingg22.api.vacunas.panama.modules.persona.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DireccionDto
import io.github.kingg22.api.vacunas.panama.modules.persona.domain.PersonaModel
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.UsuarioDto
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo
import io.mcarle.konvert.api.Mapping
import io.quarkus.runtime.annotations.RegisterForReflection
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
@RegisterForReflection
@KonvertTo(Persona::class, [Mapping("estado", expression = "estado ?: PersonaDto.DEFAULT_ESTADO")])
@KonvertTo(PersonaModel::class)
@KonvertFrom(Persona::class)
@JvmRecord
data class PersonaDto(
    val id: UUID? = null,

    @all:Size(max = 15)
    @all:Pattern(
        regexp = "^(PE|E|N|[23456789](?:AV|PI)?|1[0123]?(?:AV|PI)?)-(\\d{1,4})-(\\d{1,6})$",
        flags = [Pattern.Flag.CASE_INSENSITIVE],
        message = "El formato de la cédula no es válido",
    )
    val cedula: String? = null,

    @all:Size(max = 20)
    @all:Pattern(
        regexp = "^[A-Z0-9]{5,20}$",
        flags = [Pattern.Flag.CASE_INSENSITIVE],
        message = "El formato del pasaporte no es válido",
    )
    val pasaporte: String? = null,

    @all:Size(max = 100)
    val nombre: String? = null,

    @all:Size(max = 100)
    val nombre2: String? = null,

    @all:Size(max = 100)
    val apellido1: String? = null,

    @all:Size(max = 100)
    val apellido2: String? = null,

    @all:Size(max = 254)
    @all:Email
    val correo: String? = null,

    @all:Size(max = 15)
    @all:Pattern(regexp = "^\\+\\d{1,14}$", message = "El formato del teléfono no es válido")
    val telefono: String? = null,

    @all:JsonProperty(value = "fecha_nacimiento")
    @all:PastOrPresent
    val fechaNacimiento: LocalDateTime? = null,

    val edad: Short? = null,

    @all:Size(max = 1)
    val sexo: String? = null,

    @all:Size(max = 50)
    val estado: String? = null,

    @all:Valid
    val direccion: DireccionDto = DireccionDto(),

    @all:Valid
    val usuario: UsuarioDto? = null,
) : Serializable {
    companion object {
        const val DEFAULT_ESTADO = "NO_VALIDADO"
    }
}
