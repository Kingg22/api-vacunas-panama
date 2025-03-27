package io.github.kingg22.api.vacunas.panama.web.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.annotation.Nullable
import jakarta.validation.constraints.PastOrPresent
import java.io.Serializable
import java.time.LocalDateTime

/**
 * DTO for [io.github.kingg22.api.vacunas.panama.persistence.entity.Sede]
 */
class SedeDto @JvmOverloads constructor(
    val region: String? = null,

    @Nullable
    @JsonProperty(value = "created_at")
    @PastOrPresent
    val createdAt: LocalDateTime? = null,

    @JsonProperty(value = "updated_at")
    @PastOrPresent
    val updatedAt: LocalDateTime? = null,
) : EntidadDto(),
    Serializable
