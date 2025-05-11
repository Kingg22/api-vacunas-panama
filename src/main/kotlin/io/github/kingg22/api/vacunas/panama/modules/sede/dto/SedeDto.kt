package io.github.kingg22.api.vacunas.panama.modules.sede.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.modules.common.dto.EntidadDto
import io.github.kingg22.api.vacunas.panama.modules.sede.domain.SedeModel
import io.github.kingg22.api.vacunas.panama.modules.sede.entity.Sede
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo
import io.mcarle.konvert.api.Mapping
import io.quarkus.runtime.annotations.RegisterForReflection
import jakarta.validation.Valid
import jakarta.validation.constraints.PastOrPresent
import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC

/** DTO for [io.github.kingg22.api.vacunas.panama.modules.sede.entity.Sede] */
@RegisterForReflection
@KonvertTo(Sede::class, [Mapping("id", ignore = true)])
@KonvertTo(
    SedeModel::class,
    mappings = [
        Mapping(
            "nombre",
            expression =
            "entidad.nombre ?: io.github.kingg22.api.vacunas.panama.modules.common.dto.EntidadDto.DEFAULT_NOMBRE",
        ),
        Mapping("dependencia", source = "entidad.dependencia"),
    ],
)
@KonvertFrom(Sede::class)
@JvmRecord
data class SedeDto(
    @all:Valid
    val entidad: EntidadDto,

    val region: String? = null,

    @all:JsonProperty(value = "created_at")
    @all:PastOrPresent
    val createdAt: LocalDateTime = LocalDateTime.now(UTC),

    @all:JsonProperty(value = "updated_at")
    @all:PastOrPresent
    val updatedAt: LocalDateTime? = null,
) : Serializable {
    companion object
}
