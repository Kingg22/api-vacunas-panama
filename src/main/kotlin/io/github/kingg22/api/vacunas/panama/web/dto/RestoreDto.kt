package io.github.kingg22.api.vacunas.panama.web.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDate

/** DTO for restore password only for [io.github.kingg22.api.vacunas.panama.persistence.entity.Persona]  */
@JvmRecord
data class RestoreDto @JvmOverloads constructor(
    @NotBlank val username: String,
    @JsonProperty(value = "new_password", access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    @Size(min = 8, max = 70, message = "La contraseña no es válida")
    val newPassword: String,
    @JsonProperty(value = "fecha_nacimiento")
    @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
    val fechaNacimiento: LocalDate? = null,
) : Serializable {
    override fun toString() = RestoreDto::class.simpleName + "(username: $username, fechaNacimiento: $fechaNacimiento)"
}
