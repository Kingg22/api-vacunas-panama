package io.github.kingg22.api_vacunas_panama.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.kingg22.api_vacunas_panama.persistence.entity.Usuario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for {@link Usuario}
 */
public record UsuarioDto(
        UUID id,
        String username,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @Size(min = 8, max = 70, message = "La contraseña no es válida")
        String password,
        @JsonProperty(value = "created_at")
        LocalDateTime createdAt,
        @JsonProperty(value = "updated_at")
        LocalDateTime updatedAt,
        @JsonProperty(value = "last_used")
        LocalDateTime lastUsed,
        @NotEmpty(message = "Los roles no puede estar vacíos")
        @Valid
        Set<RolDto> roles) implements Serializable {
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + "id: " + id +
                ", username: " + username + ", createdAt: " + createdAt + ", updatedAt: " + updatedAt + ", lastUsed: " + lastUsed +
                ", roles: [" + roles.toString() + "])";
    }
}
