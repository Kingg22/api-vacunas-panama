package io.github.kingg22.api.vacunas.panama.modules.usuario.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/** DTO for Login in the API  */
@JvmRecord
data class LoginDto(
    @field:NotBlank(message = "La identificación es requerido")
    @param:NotBlank(message = "La identificación es requerido")
    val username: String,

    @field:JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @param:JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @field:NotBlank
    @param:NotBlank
    @field:Size(min = 8, max = 70, message = "La contraseña no es válida")
    @param:Size(min = 8, max = 70, message = "La contraseña no es válida")
    val password: String,
) {
    override fun toString() = LoginDto::class.simpleName + "(username: $username)"
}
