package io.github.kingg22.api.vacunas.panama.web.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/** DTO for Login in the API  */
@JvmRecord
data class LoginDto(
    @NotBlank(message = "La identificación es requerido") val username: String,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    @Size(min = 8, max = 70, message = "La contraseña no es válida")
    val password: String,
) {
    override fun toString() = LoginDto::class.simpleName + "(username: $username)"
}
