package io.github.kingg22.api.vacunas.panama.modules.common.dto

import io.github.kingg22.api.vacunas.panama.modules.common.entity.Entidad
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DireccionDto
import io.github.kingg22.api.vacunas.panama.modules.persona.dto.PersonaDto.Companion.DEFAULT_ESTADO
import io.mcarle.konvert.api.KonvertTo
import io.mcarle.konvert.api.Mapping
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.util.UUID

/** DTO for [io.github.kingg22.api.vacunas.panama.modules.common.entity.Entidad] */
@JvmRecord
@KonvertTo(Entidad::class, mappings = [Mapping(target = "nombre", expression = "nombre ?: EntidadDto.DEFAULT_NOMBRE")])
data class EntidadDto(
    val id: UUID? = null,

    @field:Size(max = 100) @param:Size(max = 100) val nombre: String? = null,

    @field:Size(max = 254) @param:Size(max = 254) @field:Email @param:Email val correo: String? = null,

    @field:Size(max = 15)
    @param:Size(max = 15)
    @field:Pattern(regexp = "^\\+\\d{1,14}$", message = "El formato del teléfono no es válido")
    @param:Pattern(regexp = "^\\+\\d{1,14}$", message = "El formato del teléfono no es válido")
    val telefono: String? = null,

    @field:Size(max = 13) @param:Size(max = 13) val dependencia: String? = null,

    @field:Size(max = 50) @param:Size(max = 50) @field:NotBlank @param:NotBlank val estado: String = DEFAULT_ESTADO,

    val disabled: Boolean = false,

    @field:Valid @param:Valid val direccion: DireccionDto = DireccionDto(),
) : Serializable {
    companion object {
        const val DEFAULT_NOMBRE = "Por registrar"
    }
}
