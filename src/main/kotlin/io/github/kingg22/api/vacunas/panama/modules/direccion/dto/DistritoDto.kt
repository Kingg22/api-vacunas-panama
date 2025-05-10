package io.github.kingg22.api.vacunas.panama.modules.direccion.dto

import io.github.kingg22.api.vacunas.panama.modules.direccion.domain.DistritoModel
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Distrito
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo
import io.quarkus.runtime.annotations.RegisterForReflection
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import java.io.Serializable

/** DTO for [io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Distrito] */
@RegisterForReflection
@KonvertTo(Distrito::class)
@KonvertTo(DistritoModel::class)
@KonvertFrom(Distrito::class)
@JvmRecord
data class DistritoDto(
    val id: Short? = null,

    @all:Size(max = 100)
    val nombre: String = DEFAULT_DISTRITO,

    @all:Valid
    val provincia: ProvinciaDto = ProvinciaDto(id = 0),
) : Serializable {
    companion object {
        const val DEFAULT_DISTRITO = "Por registrar"
    }
}
