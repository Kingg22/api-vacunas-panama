package io.github.kingg22.api.vacunas.panama.modules.direccion.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Provincia
import io.mcarle.konvert.api.KonvertTo
import jakarta.validation.constraints.Size
import java.io.Serializable

/** DTO for [io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Provincia] */
@JvmRecord
@KonvertTo(Provincia::class)
data class ProvinciaDto(
    val id: Short? = null,

    @field:JsonProperty(defaultValue = DEFAULT_PROVINCIA)
    @param:JsonProperty(defaultValue = DEFAULT_PROVINCIA)
    @field:Size(max = 30)
    @param:Size(max = 30)
    val nombre: String = DEFAULT_PROVINCIA,
) : Serializable {
    companion object {
        const val DEFAULT_PROVINCIA = "Por registrar"
    }
}
