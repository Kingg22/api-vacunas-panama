package io.github.kingg22.api.vacunas.panama.web.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime

/** DTO for [io.github.kingg22.api.vacunas.panama.persistence.entity.Fabricante]  */
class FabricanteDto :
    EntidadDto(),
    Serializable {

    @Size(max = 50)
    @Pattern(
        regexp = "^.+/DNFD$",
        flags = [Pattern.Flag.CASE_INSENSITIVE],
        message = "La licencia_fabricante no es válida",
    )
    val licencia: String? = null

    @JsonProperty(value = "contacto_nombre")
    @Size(max = 100)
    val contactoNombre: String? = null

    @JsonProperty(value = "contacto_correo")
    @Size(max = 254)
    @Email
    val contactoCorreo: String? = null

    @JsonProperty(value = "contacto_telefono")
    @Size(max = 15)
    @Pattern(regexp = "^\\+\\d{1,14}$", message = "El formato del teléfono no es válido")
    val contactoTelefono: String? = null

    @JsonProperty(value = "created_at")
    @PastOrPresent
    val createdAt: LocalDateTime? = null

    @JsonProperty(value = "updated_at")
    @PastOrPresent
    val updatedAt: LocalDateTime? = null

    @Valid
    val usuario: UsuarioDto? = null
}
