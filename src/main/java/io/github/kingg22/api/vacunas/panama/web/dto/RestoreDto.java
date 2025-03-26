package io.github.kingg22.api.vacunas.panama.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.kingg22.api.vacunas.panama.persistence.entity.Persona;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * DTO for restore password only for {@link Persona}
 */
public record RestoreDto(
        @NotNull @NotBlank(message = "La identificación es requerido") String username,
        @NotNull
                @NotBlank
                @Size(min = 8, max = 70, message = "La contraseña no es válida")
                @JsonProperty(value = "new_password", access = JsonProperty.Access.WRITE_ONLY)
                String newPassword,
        @NotNull
                @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
                @JsonProperty(value = "fecha_nacimiento")
                LocalDate fechaNacimiento) {
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + "username: " + username + ", fechaNacimiento: " + fechaNacimiento
                + ")";
    }
}
