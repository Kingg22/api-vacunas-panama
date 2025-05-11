package io.github.kingg22.api.vacunas.panama.modules.usuario.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.runtime.annotations.RegisterForReflection
import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Pattern.Flag.CASE_INSENSITIVE
import jakarta.validation.constraints.Size
import java.io.Serializable

@RegisterForReflection
@JvmRecord
data class RegisterUserDto(
    @all:Valid
    val usuario: UsuarioDto,

    @all:Size(max = 15)
    @all:Pattern(
        regexp = "^(PE|E|N|[23456789](?:AV|PI)?|1[0123]?(?:AV|PI)?)-(\\d{1,4})-(\\d{1,6})$",
        flags = [CASE_INSENSITIVE],
        message = "El formato de la cédula no es válido",
    )
    val cedula: String? = null,

    @all:Size(max = 20)
    @all:Pattern(
        regexp = "^[A-Z0-9]{5,20}$",
        flags = [CASE_INSENSITIVE],
        message = "El formato del pasaporte no es válido",
    )
    val pasaporte: String? = null,

    @all:JsonProperty(value = "licencia_fabricante")
    @all:Size(max = 50)
    @all:Pattern(regexp = "^.+/DNFD$", flags = [CASE_INSENSITIVE], message = "licencia_fabricante no es válida")
    val licenciaFabricante: String? = null,
) : Serializable {
    init {
        require(cedula != null || pasaporte != null || licenciaFabricante != null) {
            "Al menos una identificación es requerida. Opciones: [cedula, pasaporte, licencia_fabricante]"
        }
    }
}
