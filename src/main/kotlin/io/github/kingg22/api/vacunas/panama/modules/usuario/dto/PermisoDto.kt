package io.github.kingg22.api.vacunas.panama.modules.usuario.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Permiso
import io.mcarle.konvert.api.KonvertTo
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC

/** DTO for [io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Permiso] */
@JvmRecord
@KonvertTo(Permiso::class)
data class PermisoDto(
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
    val createdAt: LocalDateTime = LocalDateTime.now(UTC),

    @field:JsonProperty(value = "updated_at", access = JsonProperty.Access.READ_ONLY)
    @param:JsonProperty(value = "updated_at", access = JsonProperty.Access.READ_ONLY)
    @field:PastOrPresent
    @param:PastOrPresent
    val updatedAt: LocalDateTime? = null,
) : Serializable
