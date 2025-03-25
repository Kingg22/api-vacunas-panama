package io.github.kingg22.api_vacunas_panama.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.kingg22.api_vacunas_panama.persistence.entity.Permiso;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link Permiso}
 */
public record PermisoDto(Short id,
                         @Size(max = 100) @Pattern(regexp = "\\s*|\\S.*", message = "El nombre del permiso es requerido") String nombre,
                         @Size(max = 100) String descripcion,
                         @PastOrPresent @JsonProperty(value = "created_at") LocalDateTime createdAt,
                         @PastOrPresent @JsonProperty(value = "updated_at") LocalDateTime updatedAt) implements Serializable {
}
