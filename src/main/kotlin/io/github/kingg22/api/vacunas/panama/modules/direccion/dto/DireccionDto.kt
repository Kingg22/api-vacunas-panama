package io.github.kingg22.api.vacunas.panama.modules.direccion.dto

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.modules.direccion.domain.DireccionModel
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo
import io.quarkus.runtime.annotations.RegisterForReflection
import jakarta.validation.Valid
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.util.UUID

/** DTO for [io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion] */
@RegisterForReflection
@KonvertTo(Direccion::class)
@KonvertTo(DireccionModel::class)
@KonvertFrom(Direccion::class)
@JvmRecord
data class DireccionDto(
    val id: UUID? = null,

    @all:Size(
        max = 150,
        message = "La indicaciones de la dirección es muy larga, no incluya la provincia y distrito",
    )
    @all:JsonAlias("direccion")
    val descripcion: String = DEFAULT_DIRECCION,

    @all:Valid
    val distrito: DistritoDto = DistritoDto(id = 0),

    @all:JsonProperty(value = "created_at")
    @all:PastOrPresent
    val createdAt: LocalDateTime = LocalDateTime.now(UTC),

    @all:JsonProperty(value = "updated_at")
    @all:PastOrPresent
    val updatedAt: LocalDateTime? = null,
) : Serializable {
    companion object {
        const val DEFAULT_DIRECCION = "Por registrar"
    }
}
