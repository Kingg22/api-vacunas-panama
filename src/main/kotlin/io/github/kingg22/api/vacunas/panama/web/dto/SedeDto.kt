package io.github.kingg22.api.vacunas.panama.web.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonUnwrapped
import jakarta.annotation.Nullable
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

/** DTO for [io.github.kingg22.api.vacunas.panama.persistence.entity.Sede] */
@JvmRecord
data class SedeDto @JvmOverloads constructor(
    @field:JsonUnwrapped @param:JsonUnwrapped val entidad: EntidadDto,

    val region: String? = null,

    @field:Nullable
    @param:Nullable
    @field:JsonProperty(value = "created_at")
    @param:JsonProperty(value = "created_at")
    @field:PastOrPresent
    @param:PastOrPresent
    val createdAt: LocalDateTime? = null,

    @field:JsonProperty(value = "updated_at")
    @param:JsonProperty(value = "updated_at")
    @field:PastOrPresent
    @param:PastOrPresent
    val updatedAt: LocalDateTime? = null,
) : Serializable {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    constructor(
        id: UUID? = null,
        @Size(max = 100) nombre: String? = null,
        @Size(max = 254) @Email correo: String? = null,
        @Size(max = 15)
        @Pattern(regexp = "^\\+\\d{1,14}$", message = "El formato del teléfono no es válido")
        telefono: String? = null,
        @Size(max = 13) dependencia: String? = null,
        @Size(max = 50) @NotBlank estado: String? = null,
        disabled: Boolean = false,
        @Valid direccion: DireccionDto? = null,
        region: String? = null,
        @Nullable @JsonProperty(value = "created_at") @PastOrPresent createdAt: LocalDateTime? = null,
        @JsonProperty(value = "updated_at") @PastOrPresent updatedAt: LocalDateTime? = null,
    ) : this(
        entidad = EntidadDto(
            id = id,
            nombre = nombre,
            correo = correo,
            telefono = telefono,
            dependencia = dependencia,
            estado = estado,
            disabled = disabled,
            direccion = direccion,
        ),
        region = region,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
