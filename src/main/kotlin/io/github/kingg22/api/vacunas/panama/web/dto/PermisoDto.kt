package io.github.kingg22.api.vacunas.panama.web.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime

/** DTO for [io.github.kingg22.api.vacunas.panama.persistence.entity.Permiso] */
@JvmRecord
data class PermisoDto @JvmOverloads constructor(
    val id: Short? = null,

    @field:Size(max = 100)
    @param:Size(max = 100)
    @field:Pattern(regexp = "\\s*|\\S.*", message = "El nombre del permiso es requerido")
    @param:Pattern(regexp = "\\s*|\\S.*", message = "El nombre del permiso es requerido")
    val nombre: String,

    @field:Size(max = 100) @param:Size(max = 100) val descripcion: String? = null,

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
) : Serializable
