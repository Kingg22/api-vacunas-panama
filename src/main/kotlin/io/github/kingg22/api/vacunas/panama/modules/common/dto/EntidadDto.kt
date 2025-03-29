package io.github.kingg22.api.vacunas.panama.modules.common.dto

import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DireccionDto
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.util.UUID

/** DTO for [io.github.kingg22.api.vacunas.panama.persistence.entity.Entidad] */
@JvmRecord
data class EntidadDto @JvmOverloads constructor(
    val id: UUID? = null,

    @field:Size(max = 100) @param:Size(max = 100) val nombre: String? = null,

    @field:Size(max = 254) @param:Size(max = 254) @field:Email @param:Email val correo: String? = null,

    @field:Size(max = 15)
    @param:Size(max = 15)
    @field:Pattern(regexp = "^\\+\\d{1,14}$", message = "El formato del teléfono no es válido")
    @param:Pattern(regexp = "^\\+\\d{1,14}$", message = "El formato del teléfono no es válido")
    val telefono: String? = null,

    @field:Size(max = 13) @param:Size(max = 13) val dependencia: String? = null,

    @field:Size(max = 50) @param:Size(max = 50) @field:NotBlank @param:NotBlank val estado: String? = null,

    val disabled: Boolean = false,

    @field:Valid @param:Valid val direccion: DireccionDto? = null,
) : Serializable
