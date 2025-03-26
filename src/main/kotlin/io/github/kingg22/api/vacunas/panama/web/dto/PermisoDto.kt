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
    @Size(max = 100) @Pattern(regexp = "\\s*|\\S.*", message = "El nombre del permiso es requerido") val nombre: String,
    @Size(max = 100) val descripcion: String? = null,
    @JsonProperty(value = "created_at") @PastOrPresent val createdAt: LocalDateTime? = null,
    @JsonProperty(value = "updated_at") @PastOrPresent val updatedAt: LocalDateTime? = null,
) : Serializable
