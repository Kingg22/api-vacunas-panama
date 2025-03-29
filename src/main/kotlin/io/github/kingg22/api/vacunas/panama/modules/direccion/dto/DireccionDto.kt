package io.github.kingg22.api.vacunas.panama.modules.direccion.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion
import io.mcarle.konvert.api.KonvertTo
import jakarta.validation.Valid
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

/** DTO for [io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion] */
@JvmRecord
@KonvertTo(Direccion::class)
data class DireccionDto @JvmOverloads constructor(
    val id: UUID? = null,

    @field:JsonProperty(defaultValue = DEFAULT_DIRECCION)
    @param:JsonProperty(defaultValue = DEFAULT_DIRECCION)
    @field:Size(
        max = 150,
        message = "La indicaciones de la dirección es muy larga, no incluya la provincia y distrito",
    )
    @param:Size(
        max = 150,
        message = "La indicaciones de la dirección es muy larga, no incluya la provincia y distrito",
    )
    val direccion: String = DEFAULT_DIRECCION,

    @field:Valid @param:Valid val distrito: DistritoDto? = null,

    @field:JsonProperty(value = "created_at")
    @param:JsonProperty(value = "created_at")
    @field:PastOrPresent
    @param:PastOrPresent
    val createdAt: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC),

    @field:JsonProperty(value = "updated_at")
    @param:JsonProperty(value = "updated_at")
    @field:PastOrPresent
    @param:PastOrPresent
    val updatedAt: LocalDateTime? = null,
) : Serializable {
    companion object {
        const val DEFAULT_DIRECCION = "Por registrar"
    }
}
