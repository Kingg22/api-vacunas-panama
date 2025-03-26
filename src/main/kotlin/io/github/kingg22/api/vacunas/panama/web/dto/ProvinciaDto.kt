package io.github.kingg22.api.vacunas.panama.web.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Size
import java.io.Serializable

/** DTO for [io.github.kingg22.api.vacunas.panama.persistence.entity.Provincia] */
@JvmRecord
data class ProvinciaDto @JvmOverloads constructor(
    val id: Short? = null,
    @JsonProperty(defaultValue = DEFAULT_PROVINCIA) @Size(max = 30) val nombre: String = DEFAULT_PROVINCIA,
) : Serializable {
    companion object {
        private const val DEFAULT_PROVINCIA = "Por registrar"
    }
}
