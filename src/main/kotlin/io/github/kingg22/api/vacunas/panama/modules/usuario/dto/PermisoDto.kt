package io.github.kingg22.api.vacunas.panama.modules.usuario.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Permiso
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo
import io.quarkus.runtime.annotations.RegisterForReflection
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC

/** DTO for [io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Permiso] */
@RegisterForReflection
@KonvertTo(Permiso::class)
@KonvertFrom(Permiso::class)
@JvmRecord
data class PermisoDto(
    val id: Short? = null,

    @all:Size(max = 100)
    @all:Pattern(regexp = "\\s*|\\S.*", message = "El nombre del permiso es requerido")
    val nombre: String,

    @all:Size(max = 100)
    val descripcion: String? = null,

    @all:JsonProperty(value = "created_at")
    @all:PastOrPresent
    val createdAt: LocalDateTime = LocalDateTime.now(UTC),

    @all:JsonProperty(value = "updated_at")
    @all:PastOrPresent
    val updatedAt: LocalDateTime? = null,
) : Serializable {
    companion object
}
