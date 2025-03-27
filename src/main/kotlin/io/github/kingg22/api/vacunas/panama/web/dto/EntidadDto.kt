package io.github.kingg22.api.vacunas.panama.web.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.util.UUID

/** DTO for [io.github.kingg22.api.vacunas.panama.persistence.entity.Entidad] */
@JvmRecord
data class EntidadDto @JvmOverloads constructor(
    val id: UUID? = null,

    @Size(max = 100)
    val nombre: String? = null,

    @Size(max = 254)
    @Email
    val correo: String? = null,

    @Size(max = 15)
    @Pattern(regexp = "^\\+\\d{1,14}$", message = "El formato del teléfono no es válido")
    val telefono: String? = null,

    @Size(max = 13)
    val dependencia: String? = null,

    @Size(max = 50)
    @NotBlank
    val estado: String? = null,

    val disabled: Boolean = false,

    @Valid
    val direccion: DireccionDto? = null,
) : Serializable
