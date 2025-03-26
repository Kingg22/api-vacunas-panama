package io.github.kingg22.api.vacunas.panama.web.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

/** DTO for [io.github.kingg22.api.vacunas.panama.persistence.entity.Direccion] */
@JvmRecord
data class DireccionDto @JvmOverloads constructor(
    val id: UUID? = null,
    @JsonProperty(defaultValue = DEFAULT_DIRECCION)
    @Size(
        max = 150,
        message = "La indicaciones de la dirección es muy larga, no incluya la provincia y distrito",
    ) val direccion: String = DEFAULT_DIRECCION,
    val distrito: @Valid DistritoDto? = null,
    @JsonProperty(value = "created_at") @PastOrPresent val createdAt: LocalDateTime? = null,
    @JsonProperty(value = "updated_at") @PastOrPresent val updatedAt: LocalDateTime? = null,
) : Serializable {
    companion object {
        private const val DEFAULT_DIRECCION = "Por registrar"
    }
}
