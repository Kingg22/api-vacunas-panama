package io.github.kingg22.api.vacunas.panama.web.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.persistence.entity.Provincia
import io.mcarle.konvert.api.KonvertTo
import jakarta.validation.constraints.Size
import java.io.Serializable

/** DTO for [io.github.kingg22.api.vacunas.panama.persistence.entity.Provincia] */
@JvmRecord
@KonvertTo(Provincia::class)
data class ProvinciaDto @JvmOverloads constructor(
    val id: Short? = null,
    @JsonProperty(defaultValue = DEFAULT_PROVINCIA) @Size(max = 30) val nombre: String = DEFAULT_PROVINCIA,
) : Serializable {
    companion object {
        const val DEFAULT_PROVINCIA = "Por registrar"
    }
}
