package io.github.kingg22.api.vacunas.panama.modules.direccion.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Distrito
import io.mcarle.konvert.api.KonvertTo
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import java.io.Serializable

/** DTO for [io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Distrito] */
@JvmRecord
@KonvertTo(Distrito::class)
data class DistritoDto @JvmOverloads constructor(
    val id: Short? = null,

    @field:JsonProperty(defaultValue = DEFAULT_DISTRITO)
    @param:JsonProperty(defaultValue = DEFAULT_DISTRITO)
    @field:Size(max = 100)
    @param:Size(max = 100)
    val nombre: String = DEFAULT_DISTRITO,

    @field:Valid @param:Valid val provincia: ProvinciaDto = ProvinciaDto(),
) : Serializable {
    companion object {
        const val DEFAULT_DISTRITO = "Por registrar"
    }
}
