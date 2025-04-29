package io.github.kingg22.api.vacunas.panama.modules.fabricante.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.modules.common.dto.EntidadDto
import io.github.kingg22.api.vacunas.panama.modules.fabricante.entity.Fabricante
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
import java.time.ZoneOffset.UTC

/** DTO for [io.github.kingg22.api.vacunas.panama.modules.fabricante.entity.Fabricante]  */
@JvmRecord
@KonvertTo(Fabricante::class, [Mapping("licencia", expression = "licencia ?: \"\"")])
data class FabricanteDto(
    @field:Valid @param:Valid val entidad: EntidadDto,

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
    val createdAt: LocalDateTime = LocalDateTime.now(UTC),

    @field:JsonProperty(value = "updated_at", access = JsonProperty.Access.READ_ONLY)
    @param:JsonProperty(value = "updated_at", access = JsonProperty.Access.READ_ONLY)
    @field:PastOrPresent
    @param:PastOrPresent
    val updatedAt: LocalDateTime? = null,

    @field:Valid @param:Valid val usuario: UsuarioDto? = null,
) : Serializable
