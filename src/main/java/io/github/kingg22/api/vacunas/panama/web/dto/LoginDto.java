package io.github.kingg22.api.vacunas.panama.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for Login in the API
 */
public record LoginDto(
        @NotBlank(message = "La identificación es requerido") String username,
        @NotBlank(message = "La contraseña es requerida")
                @Size(min = 8, max = 70, message = "La contraseña no es válida")
                @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
                String password) {
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + "username: " + username + ")";
    }
}
