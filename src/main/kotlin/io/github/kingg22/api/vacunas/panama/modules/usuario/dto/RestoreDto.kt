package io.github.kingg22.api.vacunas.panama.modules.usuario.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.runtime.annotations.RegisterForReflection
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDate

/** DTO for restore password only for [io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona]  */
@RegisterForReflection
@JvmRecord
data class RestoreDto(
    @all:NotBlank
    val username: String,

    @all:JsonProperty(value = "new_password")
    @all:NotBlank
    @all:Size(min = 8, max = 70, message = "La contraseña no es válida")
    val newPassword: String,

    @all:JsonProperty(value = "fecha_nacimiento")
    @all:PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
    @all:NotNull
    val fechaNacimiento: LocalDate,
) : Serializable {
    override fun toString() = RestoreDto::class.simpleName + "(username: $username, fechaNacimiento: $fechaNacimiento)"
}
