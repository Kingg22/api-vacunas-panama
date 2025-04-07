package io.github.kingg22.api.vacunas.panama.modules.usuario.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY
import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Pattern.Flag.CASE_INSENSITIVE
import jakarta.validation.constraints.Size
import java.io.Serializable

@JvmRecord
data class RegisterUserDto @JvmOverloads constructor(
    @param:Valid @field:Valid val usuario: UsuarioDto,

    @param:JsonProperty(access = WRITE_ONLY)
    @field:JsonProperty(access = WRITE_ONLY)
    @param:Size(max = 15)
    @field:Size(max = 15)
    @param:Pattern(
        regexp = "^(PE|E|N|[23456789](?:AV|PI)?|1[0123]?(?:AV|PI)?)-(\\d{1,4})-(\\d{1,6})$",
        flags = [CASE_INSENSITIVE],
        message = "El formato de la cédula no es válido",
    )
    @field:Pattern(
        regexp = "^(PE|E|N|[23456789](?:AV|PI)?|1[0123]?(?:AV|PI)?)-(\\d{1,4})-(\\d{1,6})$",
        flags = [CASE_INSENSITIVE],
        message = "El formato de la cédula no es válido",
    )
    val cedula: String? = null,

    @param:JsonProperty(access = WRITE_ONLY)
    @field:JsonProperty(access = WRITE_ONLY)
    @param:Size(max = 20)
    @field:Size(max = 20)
    @param:Pattern(
        regexp = "^[A-Z0-9]{5,20}$",
        flags = [CASE_INSENSITIVE],
        message = "El formato del pasaporte no es válido",
    )
    @field:Pattern(
        regexp = "^[A-Z0-9]{5,20}$",
        flags = [CASE_INSENSITIVE],
        message = "El formato del pasaporte no es válido",
    )
    val pasaporte: String? = null,

    @param:JsonProperty(value = "licencia_fabricante", access = WRITE_ONLY)
    @field:JsonProperty(value = "licencia_fabricante", access = WRITE_ONLY)
    @param:Size(max = 50)
    @field:Size(max = 50)
    @param:Pattern(regexp = "^.+/DNFD$", flags = [CASE_INSENSITIVE], message = "licencia_fabricante no es válida")
    @field:Pattern(regexp = "^.+/DNFD$", flags = [CASE_INSENSITIVE], message = "licencia_fabricante no es válida")
    val licenciaFabricante: String? = null,
) : Serializable {
    init {
        require(cedula != null || pasaporte != null || licenciaFabricante != null) {
            "Al menos una identificación es requerida. Opciones: [cedula, pasaporte, licencia_fabricante]"
        }
    }
}
