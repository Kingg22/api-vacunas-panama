package io.github.kingg22.api.vacunas.panama.web.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

/** DTO for [io.github.kingg22.api.vacunas.panama.persistence.entity.Fabricante]  */
@JvmRecord
data class FabricanteDto @JvmOverloads constructor(
    val entidad: EntidadDto,
    @Size(max = 50)
    @Pattern(
        regexp = "^.+/DNFD$",
        flags = [Pattern.Flag.CASE_INSENSITIVE],
        message = "La licencia_fabricante no es válida",
    )
    val licencia: String? = null,

    @JsonProperty(value = "contacto_nombre")
    @Size(max = 100)
    val contactoNombre: String? = null,

    @JsonProperty(value = "contacto_correo")
    @Size(max = 254)
    @Email
    val contactoCorreo: String? = null,

    @JsonProperty(value = "contacto_telefono")
    @Size(max = 15)
    @Pattern(regexp = "^\\+\\d{1,14}$", message = "El formato del teléfono no es válido")
    val contactoTelefono: String? = null,

    @JsonProperty(value = "created_at")
    @PastOrPresent
    val createdAt: LocalDateTime? = null,

    @JsonProperty(value = "updated_at")
    @PastOrPresent
    val updatedAt: LocalDateTime? = null,

    @Valid
    val usuario: UsuarioDto? = null,
) : Serializable {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    constructor(
        id: UUID? = null,

        @Size(max = 100)
        nombre: String? = null,

        @Size(max = 254)
        @Email
        correo: String? = null,

        @Size(max = 15)
        @Pattern(regexp = "^\\+\\d{1,14}$", message = "El formato del teléfono no es válido")
        telefono: String? = null,

        @Size(max = 13)
        dependencia: String? = null,

        @Size(max = 50)
        @NotBlank
        estado: String? = null,

        disabled: Boolean = false,

        @Valid
        direccion: DireccionDto? = null,

        @Size(max = 50)
        @Pattern(
            regexp = "^.+/DNFD$",
            flags = [Pattern.Flag.CASE_INSENSITIVE],
            message = "La licencia_fabricante no es válida",
        )
        licencia: String? = null,

        @JsonProperty(value = "contacto_nombre")
        @Size(max = 100)
        contactoNombre: String? = null,

        @JsonProperty(value = "contacto_correo")
        @Size(max = 254)
        @Email
        contactoCorreo: String? = null,

        @JsonProperty(value = "contacto_telefono")
        @Size(max = 15)
        @Pattern(regexp = "^\\+\\d{1,14}$", message = "El formato del teléfono no es válido")
        contactoTelefono: String? = null,

        @JsonProperty(value = "created_at")
        @PastOrPresent
        createdAt: LocalDateTime? = null,

        @JsonProperty(value = "updated_at")
        @PastOrPresent
        updatedAt: LocalDateTime? = null,

        @Valid
        usuario: UsuarioDto? = null,
    ) : this(
        entidad = EntidadDto(
            id = id,
            nombre = nombre,
            correo = correo,
            telefono = telefono,
            dependencia = dependencia,
            estado = estado,
            disabled = disabled,
            direccion = direccion,
        ),
        licencia = licencia,
        contactoNombre = contactoNombre,
        contactoCorreo = contactoCorreo,
        contactoTelefono = contactoTelefono,
        createdAt = createdAt,
        updatedAt = updatedAt,
        usuario = usuario,
    )
}
