package io.github.kingg22.api.vacunas.panama.modules.fabricante.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.modules.common.dto.EntidadDto
import io.github.kingg22.api.vacunas.panama.modules.fabricante.entity.Fabricante
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
import java.time.ZoneOffset.UTC

/**
 * DTO for [io.github.kingg22.api.vacunas.panama.modules.fabricante.entity.Fabricante]
 *
 * _Warning_:
 * - [licencia] is required in Entity, default value: `""`
 */
@RegisterForReflection
@KonvertTo(Fabricante::class, [Mapping("licencia", expression = "licencia ?: \"\"")])
@KonvertFrom(Fabricante::class)
@JvmRecord
data class FabricanteDto(
    @all:Valid
    val entidad: EntidadDto,

    @all:Size(max = 50)
    @all:Pattern(
        regexp = "^.+/DNFD$",
        flags = [Pattern.Flag.CASE_INSENSITIVE],
        message = "La licencia_fabricante no es válida",
    )
    val licencia: String? = null,

    @all:JsonProperty(value = "contacto_nombre")
    @all:Size(max = 100)
    val contactoNombre: String? = null,

    @all:JsonProperty(value = "contacto_correo")
    @all:Size(max = 254)
    @all:Email
    val contactoCorreo: String? = null,

    @all:JsonProperty(value = "contacto_telefono")
    @all:Size(max = 15)
    @all:Pattern(regexp = "^\\+\\d{1,14}$", message = "El formato del teléfono no es válido")
    val contactoTelefono: String? = null,

    @all:JsonProperty(value = "created_at")
    @all:PastOrPresent
    val createdAt: LocalDateTime = LocalDateTime.now(UTC),

    @all:JsonProperty(value = "updated_at")
    @all:PastOrPresent
    val updatedAt: LocalDateTime? = null,

    @field:Valid @param:Valid val usuario: UsuarioDto? = null,
) : Serializable {
    companion object
}
