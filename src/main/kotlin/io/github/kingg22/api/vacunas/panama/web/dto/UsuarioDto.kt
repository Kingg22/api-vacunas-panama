package io.github.kingg22.api.vacunas.panama.web.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

/** DTO for [io.github.kingg22.api.vacunas.panama.persistence.entity.Usuario] */
@JvmRecord
data class UsuarioDto @JvmOverloads constructor(
    val id: UUID? = null,
    val username: String? = null,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 8, max = 70, message = "La contraseña no es válida")
    val password: String,
    @JsonProperty(value = "created_at") val createdAt: LocalDateTime? = null,
    @JsonProperty(value = "updated_at") val updatedAt: LocalDateTime? = null,
    @JsonProperty(value = "last_used") val lastUsed: LocalDateTime? = null,
    @NotEmpty(message = "Los roles no puede estar vacíos") @Valid val roles: Set<RolDto>? = null,
) : Serializable {
    override fun toString() = UsuarioDto::class.simpleName +
        "(id: $id, username: $username, createdAt: $createdAt, updatedAt: $updatedAt, lastUsed: $lastUsed, roles: $roles)"
}
