package io.github.kingg22.api.vacunas.panama.modules.usuario.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDate

/** DTO for restore password only for [io.github.kingg22.api.vacunas.panama.persistence.entity.Persona]  */
@JvmRecord
data class RestoreDto @JvmOverloads constructor(
    @param:NotBlank @field:NotBlank val username: String,

    @param:JsonProperty(value = "new_password", access = JsonProperty.Access.WRITE_ONLY)
    @field:JsonProperty(value = "new_password", access = JsonProperty.Access.WRITE_ONLY)
    @param:NotBlank
    @field:NotBlank
    @param:Size(min = 8, max = 70, message = "La contraseña no es válida")
    @field:Size(min = 8, max = 70, message = "La contraseña no es válida")
    val newPassword: String,

    @param:JsonProperty(value = "fecha_nacimiento")
    @field:JsonProperty(value = "fecha_nacimiento")
    @param:PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
    @field:PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
    val fechaNacimiento: LocalDate? = null,
) : Serializable {
    override fun toString() = RestoreDto::class.simpleName + "(username: $username, fechaNacimiento: $fechaNacimiento)"
}
