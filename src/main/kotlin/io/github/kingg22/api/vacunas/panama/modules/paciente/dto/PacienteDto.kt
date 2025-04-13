package io.github.kingg22.api.vacunas.panama.modules.paciente.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonUnwrapped
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
import java.util.UUID

/** DTO for [io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente] */
@JvmRecord
data class PacienteDto(
    @field:JsonUnwrapped @param:JsonUnwrapped @field:Valid @param:Valid val persona: PersonaDto,

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

    @field:JsonProperty(value = "created_at")
    @param:JsonProperty(value = "created_at")
    @field:PastOrPresent
    @param:PastOrPresent
    val createdAt: LocalDateTime? = null,

    @field:JsonProperty(value = "updated_at")
    @param:JsonProperty(value = "updated_at")
    @field:PastOrPresent
    @param:PastOrPresent
    val updatedAt: LocalDateTime? = null,
) : Serializable {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    constructor(
        id: UUID? = null,
        @Size(max = 15)
        @Pattern(
            regexp = "^(PE|E|N|[23456789](?:AV|PI)?|1[0123]?(?:AV|PI)?)-(\\d{1,4})-(\\d{1,6})$",
            flags = [Pattern.Flag.CASE_INSENSITIVE, Pattern.Flag.MULTILINE],
            message = "El formato de la cédula no es válido",
        )
        cedula: String? = null,
        @Size(max = 20)
        @Pattern(
            regexp = "^[A-Z0-9]{5,20}$",
            flags = [Pattern.Flag.CASE_INSENSITIVE, Pattern.Flag.MULTILINE],
            message = "El formato del pasaporte no es válido",
        )
        pasaporte: String? = null,
        @Size(max = 100) nombre: String? = null,
        @Size(max = 100) nombre2: String? = null,
        @Size(max = 100) apellido1: String? = null,
        @Size(max = 100) apellido2: String? = null,
        @Size(max = 254) @Email correo: String? = null,
        @Size(max = 15)
        @Pattern(regexp = "^\\+\\d{1,14}$", message = "El formato del teléfono no es válido")
        telefono: String? = null,
        @JsonProperty(value = "fecha_nacimiento") @PastOrPresent fechaNacimiento: LocalDateTime? = null,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) edad: Short? = null,
        sexo: Char? = null,
        @Size(max = 50) estado: String? = null,
        disabled: Boolean = false,
        @Valid direccion: DireccionDto? = null,
        @Valid usuario: UsuarioDto? = null,
        @JsonProperty(value = "identificacion_temporal")
        @Size(max = 255)
        @Pattern(
            regexp =
            "^(RN(\\\\d{1,2}?)?)-(PE|E|N|[23456789](?:AV|PI)?|1[0123]?(?:AV|PI)?)-(\\\\d{1,4})-(\\\\d{1,6})$|^NI-.+$",
            flags = [Pattern.Flag.CASE_INSENSITIVE, Pattern.Flag.MULTILINE],
            message = "El formato de id temporal no es válido",
        )
        identificacionTemporal: String? = null,
        @JsonProperty(value = "created_at") @PastOrPresent createdAt: LocalDateTime? = null,
        @JsonProperty(value = "updated_at") @PastOrPresent updatedAt: LocalDateTime? = null,
    ) : this(
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
            disabled = disabled,
            direccion = direccion,
            usuario = usuario,
        ),
        identificacionTemporal = identificacionTemporal,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
