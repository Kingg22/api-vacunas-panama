package io.github.kingg22.api.vacunas.panama.web.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.persistence.entity.Distrito
import io.mcarle.konvert.api.KonvertTo
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import java.io.Serializable

/** DTO for [io.github.kingg22.api.vacunas.panama.persistence.entity.Distrito] */
@JvmRecord
@KonvertTo(Distrito::class)
data class DistritoDto @JvmOverloads constructor(
    val id: Short? = null,
    @JsonProperty(defaultValue = DEFAULT_DISTRITO) @Size(max = 100) val nombre: String = DEFAULT_DISTRITO,
    @Valid val provincia: ProvinciaDto = ProvinciaDto(),
) : Serializable {
    companion object {
        private const val DEFAULT_DISTRITO = "Por registrar"
    }
}
