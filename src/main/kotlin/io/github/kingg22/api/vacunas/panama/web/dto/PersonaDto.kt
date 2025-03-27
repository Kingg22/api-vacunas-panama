package io.github.kingg22.api.vacunas.panama.web.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

/** DTO for [io.github.kingg22.api.vacunas.panama.persistence.entity.Persona]  */
open class PersonaDto @JvmOverloads constructor(
    var id: UUID? = null,

    @Size(max = 15)
    @Pattern(
        regexp = "^(PE|E|N|[23456789](?:AV|PI)?|1[0123]?(?:AV|PI)?)-(\\d{1,4})-(\\d{1,6})$",
        flags = [Pattern.Flag.CASE_INSENSITIVE, Pattern.Flag.MULTILINE],
        message = "El formato de la cédula no es válido",
    )
    var cedula: String? = null,

    @Size(max = 20)
    @Pattern(
        regexp = "^[A-Z0-9]{5,20}$",
        flags = [Pattern.Flag.CASE_INSENSITIVE, Pattern.Flag.MULTILINE],
        message = "El formato del pasaporte no es válido",
    )
    var pasaporte: String? = null,

    @Size(max = 100)
    var nombre: String? = null,

    @Size(max = 100)
    var nombre2: String? = null,

    @Size(max = 100)
    var apellido1: String? = null,

    @Size(max = 100)
    var apellido2: String? = null,

    @Size(max = 254)
    @Email
    var correo: String? = null,

    @Size(max = 15)
    @Pattern(
        regexp = "^\\+\\d{1,14}$",
        flags = [Pattern.Flag.MULTILINE],
        message = "El formato del teléfono no es válido",
    )
    var telefono: String? = null,

    @JsonProperty(value = "fecha_nacimiento")
    @PastOrPresent
    var fechaNacimiento: LocalDateTime? = null,

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var edad: Short? = null,

    var sexo: Char? = null,

    @Size(max = 50)
    var estado: String? = null,

    var disabled: Boolean = false,

    @Valid
    var direccion: DireccionDto? = null,

    @Valid
    var usuario: UsuarioDto? = null,
) : Serializable
