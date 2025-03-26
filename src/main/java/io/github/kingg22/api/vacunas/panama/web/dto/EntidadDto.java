package io.github.kingg22.api.vacunas.panama.web.dto;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Entidad;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

/**
 * DTO for {@link Entidad}
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EntidadDto implements Serializable {
    UUID id;

    @NotNull
    @Size(max = 100)
    String nombre;

    @Size(max = 254)
    @Email
    String correo;

    @Size(max = 15)
    @Pattern(
            regexp = "^\\+\\d{1,14}$",
            flags = {Pattern.Flag.MULTILINE},
            message = "El formato del teléfono no es válido")
    String telefono;

    @Size(max = 13)
    String dependencia;

    @NotNull
    @Size(max = 50)
    @NotBlank
    String estado;

    @NotNull
    Boolean disabled = false;

    @Valid
    DireccionDto direccion;
}
