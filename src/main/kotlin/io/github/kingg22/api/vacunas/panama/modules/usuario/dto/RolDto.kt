package io.github.kingg22.api.vacunas.panama.modules.usuario.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RolDto.Companion.DEFAULT_ROL
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Rol
import io.mcarle.konvert.api.KonvertTo
import io.mcarle.konvert.api.Mapping
import jakarta.validation.Valid
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC

/**
 * DTO for [io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Rol]
 *
 * _Warning_: [RolDto.toRol] include a default value ([DEFAULT_ROL]) for [RolDto.nombre] if it is null.
 */
@JvmRecord
@KonvertTo(Rol::class, mappings = [Mapping("nombre", expression = "nombre ?: RolDto.DEFAULT_ROL")])
data class RolDto(
    val id: Short? = null,

    @param:Size(max = 100)
    @field:Size(max = 100)
    @param:Pattern(regexp = "\\s*|\\S.*", message = "El nombre del rol es requerido")
    @field:Pattern(regexp = "\\s*|\\S.*", message = "El nombre del rol es requerido")
    val nombre: String? = null,

    @param:Size(max = 100) @field:Size(max = 100) val descripcion: String? = null,

    @param:Valid @field:Valid val permisos: Set<PermisoDto> = emptySet(),

    @param:JsonProperty(value = "created_at")
    @field:JsonProperty(value = "created_at")
    @param:PastOrPresent(message = "La fecha de creación no puede ser futura")
    @field:PastOrPresent(message = "La fecha de creación no puede ser futura")
    val createdAt: LocalDateTime = LocalDateTime.now(UTC),

    @param:JsonProperty(value = "updated_at", access = JsonProperty.Access.READ_ONLY)
    @field:JsonProperty(value = "updated_at", access = JsonProperty.Access.READ_ONLY)
    @param:PastOrPresent(message = "La fecha de actualización no puede ser futura")
    @field:PastOrPresent(message = "La fecha de actualización no puede ser futura")
    val updatedAt: LocalDateTime? = null,
) : Serializable {
    companion object {
        const val DEFAULT_ROL = "PACIENTE"
    }
}
