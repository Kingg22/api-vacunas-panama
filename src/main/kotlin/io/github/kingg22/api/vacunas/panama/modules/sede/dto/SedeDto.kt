package io.github.kingg22.api.vacunas.panama.modules.sede.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.modules.common.dto.EntidadDto
import io.github.kingg22.api.vacunas.panama.modules.sede.entity.Sede
import io.mcarle.konvert.api.KonvertTo
import io.mcarle.konvert.api.Mapping
import jakarta.annotation.Nullable
import jakarta.validation.Valid
import jakarta.validation.constraints.PastOrPresent
import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC

/** DTO for [io.github.kingg22.api.vacunas.panama.modules.sede.entity.Sede] */
@JvmRecord
@KonvertTo(Sede::class, mappings = [Mapping("id", ignore = true)])
data class SedeDto(
    @field:Valid @param:Valid val entidad: EntidadDto,

    val region: String? = null,

    @field:Nullable
    @param:Nullable
    @field:JsonProperty(value = "created_at")
    @param:JsonProperty(value = "created_at")
    @field:PastOrPresent
    @param:PastOrPresent
    val createdAt: LocalDateTime = LocalDateTime.now(UTC),

    @field:JsonProperty(value = "updated_at")
    @param:JsonProperty(value = "updated_at")
    @field:PastOrPresent
    @param:PastOrPresent
    val updatedAt: LocalDateTime? = null,
) : Serializable
