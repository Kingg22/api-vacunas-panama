package io.github.kingg22.api.vacunas.panama.modules.common.dto

import io.github.kingg22.api.vacunas.panama.modules.common.entity.Entidad
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DireccionDto
import io.github.kingg22.api.vacunas.panama.modules.persona.dto.PersonaDto.Companion.DEFAULT_ESTADO
import io.mcarle.konvert.api.KonvertTo
import io.mcarle.konvert.api.Mapping
import io.quarkus.runtime.annotations.RegisterForReflection
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.util.UUID

/** DTO for [io.github.kingg22.api.vacunas.panama.modules.common.entity.Entidad] */
@RegisterForReflection
@KonvertTo(Entidad::class, mappings = [Mapping(target = "nombre", expression = "nombre ?: EntidadDto.DEFAULT_NOMBRE")])
@JvmRecord
data class EntidadDto(
    val id: UUID? = null,

    @all:Size(max = 100)
    val nombre: String? = null,

    @all:Size(max = 254)
    @all:Email
    val correo: String? = null,

    @all:Size(max = 15)
    @all:Pattern(regexp = "^\\+\\d{1,14}$", message = "El formato del teléfono no es válido")
    val telefono: String? = null,

    @all:Size(max = 13)
    val dependencia: String? = null,

    @all:Size(max = 50)
    @all:NotBlank
    val estado: String = DEFAULT_ESTADO,

    @all:Valid
    val direccion: DireccionDto = DireccionDto(),
) : Serializable {
    companion object {
        const val DEFAULT_NOMBRE = "Por registrar"
    }
}
