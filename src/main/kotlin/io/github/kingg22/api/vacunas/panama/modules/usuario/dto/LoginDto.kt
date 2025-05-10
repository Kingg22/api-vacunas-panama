package io.github.kingg22.api.vacunas.panama.modules.usuario.dto

import io.quarkus.runtime.annotations.RegisterForReflection
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.io.Serializable

/** DTO for Login in the API */
@RegisterForReflection
@JvmRecord
data class LoginDto(
    @all:NotBlank(message = "La identificación es requerido")
    val username: String,

    @all:NotBlank
    @all:Size(min = 8, max = 70, message = "La contraseña no es válida")
    val password: String,
) : Serializable {
    override fun toString() = LoginDto::class.simpleName + "(username: $username)"
}
