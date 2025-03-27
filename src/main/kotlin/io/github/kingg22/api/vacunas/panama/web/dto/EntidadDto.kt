package io.github.kingg22.api.vacunas.panama.web.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.util.UUID

/** DTO for [io.github.kingg22.api.vacunas.panama.persistence.entity.Entidad] */
open class EntidadDto @JvmOverloads constructor(
    var id: UUID? = null,

    @Size(max = 100)
    var nombre: String? = null,

    @Size(max = 254)
    @Email
    var correo: String? = null,

    @Size(max = 15)
    @Pattern(regexp = "^\\+\\d{1,14}$", message = "El formato del teléfono no es válido")
    var telefono: String? = null,

    @Size(max = 13)
    var dependencia: String? = null,

    @Size(max = 50)
    @NotBlank
    var estado: String? = null,

    var disabled: Boolean = false,

    @Valid
    var direccion: DireccionDto? = null,
) : Serializable
