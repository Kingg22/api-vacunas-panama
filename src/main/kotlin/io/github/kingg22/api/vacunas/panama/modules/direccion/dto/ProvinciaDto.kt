package io.github.kingg22.api.vacunas.panama.modules.direccion.dto

import io.github.kingg22.api.vacunas.panama.modules.direccion.domain.ProvinciaModel
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Provincia
import io.mcarle.konvert.api.KonvertTo
import io.quarkus.runtime.annotations.RegisterForReflection
import jakarta.validation.constraints.Size
import java.io.Serializable

/** DTO for [io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Provincia] */
@RegisterForReflection
@KonvertTo(Provincia::class)
@KonvertTo(ProvinciaModel::class)
@JvmRecord
data class ProvinciaDto(
    val id: Short? = null,

    @all:Size(max = 30)
    val nombre: String = DEFAULT_PROVINCIA,
) : Serializable {
    companion object {
        const val DEFAULT_PROVINCIA = "Por registrar"
    }
}
