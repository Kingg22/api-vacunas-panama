package io.github.kingg22.api.vacunas.panama.web.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonUnwrapped
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
    @field:JsonUnwrapped @param:JsonUnwrapped val entidad: EntidadDto,

    @field:Size(max = 50)
    @param:Size(max = 50)
    @field:Pattern(
        regexp = "^.+/DNFD$",
        flags = [Pattern.Flag.CASE_INSENSITIVE],
        message = "La licencia_fabricante no es válida",
    )
    @param:Pattern(
        regexp = "^.+/DNFD$",
        flags = [Pattern.Flag.CASE_INSENSITIVE],
        message = "La licencia_fabricante no es válida",
    )
    val licencia: String? = null,

    @field:JsonProperty(value = "contacto_nombre")
    @param:JsonProperty(value = "contacto_nombre")
    @field:Size(max = 100)
    @param:Size(max = 100)
    val contactoNombre: String? = null,

    @field:JsonProperty(value = "contacto_correo")
    @param:JsonProperty(value = "contacto_correo")
    @field:Size(max = 254)
    @param:Size(max = 254)
    @field:Email
    @param:Email
    val contactoCorreo: String? = null,

    @field:JsonProperty(value = "contacto_telefono")
    @param:JsonProperty(value = "contacto_telefono")
    @field:Size(max = 15)
    @param:Size(max = 15)
    @field:Pattern(regexp = "^\\+\\d{1,14}$", message = "El formato del teléfono no es válido")
    @param:Pattern(regexp = "^\\+\\d{1,14}$", message = "El formato del teléfono no es válido")
    val contactoTelefono: String? = null,

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

    @field:Valid @param:Valid val usuario: UsuarioDto? = null,
) : Serializable {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    constructor(
        id: UUID? = null,

        @Size(max = 100) nombre: String? = null,

        @Size(max = 254) @Email correo: String? = null,

        @Size(max = 15)
        @Pattern(regexp = "^\\+\\d{1,14}$", message = "El formato del teléfono no es válido")
        telefono: String? = null,

        @Size(max = 13) dependencia: String? = null,

        @Size(max = 50) @NotBlank estado: String? = null,

        disabled: Boolean = false,

        @Valid direccion: DireccionDto? = null,

        @Size(max = 50)
        @Pattern(
            regexp = "^.+/DNFD$",
            flags = [Pattern.Flag.CASE_INSENSITIVE],
            message = "La licencia_fabricante no es válida",
        )
        licencia: String? = null,

        @JsonProperty(value = "contacto_nombre") @Size(max = 100) contactoNombre: String? = null,

        @JsonProperty(value = "contacto_correo") @Size(max = 254) @Email contactoCorreo: String? = null,

        @JsonProperty(value = "contacto_telefono")
        @Size(max = 15)
        @Pattern(regexp = "^\\+\\d{1,14}$", message = "El formato del teléfono no es válido")
        contactoTelefono: String? = null,

        @JsonProperty(value = "created_at") @PastOrPresent createdAt: LocalDateTime? = null,

        @JsonProperty(value = "updated_at") @PastOrPresent updatedAt: LocalDateTime? = null,

        @Valid usuario: UsuarioDto? = null,
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
