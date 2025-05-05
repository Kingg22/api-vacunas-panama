package io.github.kingg22.api.vacunas.panama.modules.usuario.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.kingg22.api.vacunas.panama.modules.usuario.domain.UsuarioModel
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Usuario
import io.mcarle.konvert.api.KonvertTo
import io.mcarle.konvert.api.Mapping
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Size
import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.util.UUID

/** DTO for [io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Usuario] */
@JvmRecord
@KonvertTo(
    Usuario::class,
    mappings = [
        Mapping("usuario", "username"), Mapping("clave", "password"),
        Mapping("fabricante", ignore = true), Mapping("persona", ignore = true),
    ],
)
@KonvertTo(UsuarioModel::class)
data class UsuarioDto(
    val id: UUID? = null,

    val username: String? = null,

    @param:JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @field:JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @param:Size(min = 8, max = 70, message = "La contraseña no es válida")
    @field:Size(min = 8, max = 70, message = "La contraseña no es válida")
    val password: String,

    @param:JsonProperty(value = "created_at")
    @field:JsonProperty(value = "created_at")
    @field:PastOrPresent
    @param:PastOrPresent
    val createdAt: LocalDateTime = LocalDateTime.now(UTC),

    @param:JsonProperty(value = "updated_at", access = JsonProperty.Access.READ_ONLY)
    @field:JsonProperty(value = "updated_at", access = JsonProperty.Access.READ_ONLY)
    @field:PastOrPresent
    @param:PastOrPresent
    val updatedAt: LocalDateTime? = null,

    @param:JsonProperty(value = "last_used", access = JsonProperty.Access.READ_ONLY)
    @field:JsonProperty(value = "last_used", access = JsonProperty.Access.READ_ONLY)
    val lastUsed: LocalDateTime? = null,

    @param:NotEmpty(message = "Los roles no puede estar vacíos")
    @field:NotEmpty(message = "Los roles no puede estar vacíos")
    @param:Valid
    @field:Valid
    val roles: Set<RolDto>,

    @field:JsonIgnore val disabled: Boolean = true,
) : Serializable {

    override fun toString() = UsuarioDto::class.simpleName +
        "(id: $id, username: $username, createdAt: $createdAt, updatedAt: $updatedAt, lastUsed: $lastUsed, roles: $roles)"
}
