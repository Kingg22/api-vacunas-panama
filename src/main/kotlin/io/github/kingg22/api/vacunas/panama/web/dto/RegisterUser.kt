package io.github.kingg22.api.vacunas.panama.web.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.io.Serializable

@JvmRecord
data class RegisterUser @JvmOverloads constructor(
    val usuario: @Valid UsuarioDto,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(max = 15)
    @Pattern(
        regexp = "^(PE|E|N|[23456789](?:AV|PI)?|1[0123]?(?:AV|PI)?)-(\\d{1,4})-(\\d{1,6})$",
        flags = [Pattern.Flag.CASE_INSENSITIVE, Pattern.Flag.MULTILINE],
        message = "El formato de la cédula no es válido",
    )
    val cedula: String? = null,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(max = 20)
    @Pattern(
        regexp = "^[A-Z0-9]{5,20}$",
        flags = [Pattern.Flag.CASE_INSENSITIVE, Pattern.Flag.MULTILINE],
        message = "El formato del pasaporte no es válido",
    )
    val pasaporte: String? = null,
    @JsonProperty(
        value = "licencia_fabricante",
        access = JsonProperty.Access.WRITE_ONLY,
    )
    @Size(max = 50)
    @Pattern(
        regexp = "^.+/DNFD$",
        flags = [Pattern.Flag.CASE_INSENSITIVE, Pattern.Flag.MULTILINE],
        message = "licencia_fabricante no es válida",
    )
    val licenciaFabricante: String? = null,
) : Serializable {
    init {
        require(cedula != null || pasaporte != null || licenciaFabricante != null) {
            "Al menos una identificación es requerida. Opciones: [cedula, pasaporte, licencia_fabricante]"
        }
    }
}
