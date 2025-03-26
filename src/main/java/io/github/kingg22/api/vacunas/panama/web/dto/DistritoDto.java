package io.github.kingg22.api.vacunas.panama.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.kingg22.api.vacunas.panama.persistence.entity.Distrito;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import org.springframework.validation.annotation.Validated;

/**
 * DTO for {@link Distrito}
 */
@Validated
public record DistritoDto(
        Short id,
        @Size(max = 100) @JsonProperty(defaultValue = "Por registrar") String nombre,
        @Valid ProvinciaDto provincia)
        implements Serializable {}
