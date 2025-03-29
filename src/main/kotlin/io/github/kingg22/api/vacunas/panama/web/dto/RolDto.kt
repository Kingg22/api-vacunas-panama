package io.github.kingg22.api.vacunas.panama.web.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime

/** DTO for [io.github.kingg22.api.vacunas.panama.persistence.entity.Rol] */
@JvmRecord
data class RolDto @JvmOverloads constructor(
    val id: Short? = null,

    @param:Size(max = 100)
    @field:Size(max = 100)
    @param:Pattern(regexp = "\\s*|\\S.*", message = "El nombre del rol es requerido")
    @field:Pattern(regexp = "\\s*|\\S.*", message = "El nombre del rol es requerido")
    val nombre: String? = null,

    @param:Size(max = 100) @field:Size(max = 100) val descripcion: String? = null,

    @param:Valid @field:Valid val permisos: Set<PermisoDto>? = emptySet(),

    @param:JsonProperty(value = "created_at")
    @field:JsonProperty(value = "created_at")
    @param:PastOrPresent(message = "La fecha de creación no puede ser futura")
    @field:PastOrPresent(message = "La fecha de creación no puede ser futura")
    val createdAt: LocalDateTime? = null,

    @param:JsonProperty(value = "updated_at")
    @field:JsonProperty(value = "updated_at")
    @param:PastOrPresent(message = "La fecha de actualización no puede ser futura")
    @field:PastOrPresent(message = "La fecha de actualización no puede ser futura")
    val updatedAt: LocalDateTime? = null,
) : Serializable
