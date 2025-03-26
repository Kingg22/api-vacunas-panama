package io.github.kingg22.api.vacunas.panama.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.kingg22.api.vacunas.panama.persistence.entity.Fabricante;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

/**
 * DTO for {@link Fabricante}
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FabricanteDto extends EntidadDto implements Serializable {
    @NotNull
    @Size(max = 50)
    @Pattern(
            regexp = "^.+/DNFD$",
            flags = {Pattern.Flag.CASE_INSENSITIVE, Pattern.Flag.MULTILINE},
            message = "La licenciaFabricante no es válida")
    String licencia;

    @Size(max = 100)
    @JsonProperty(value = "contacto_nombre")
    String contactoNombre;

    @Size(max = 254)
    @Email
    @JsonProperty(value = "contacto_correo")
    String contactoCorreo;

    @Size(max = 15)
    @Pattern(
            regexp = "^\\+\\d{1,14}$",
            flags = {Pattern.Flag.MULTILINE},
            message = "El formato del teléfono no es válido")
    @JsonProperty(value = "contacto_telefono")
    String contactoTelefono;

    @PastOrPresent
    @JsonProperty(value = "created_at")
    LocalDateTime createdAt;

    @PastOrPresent
    @JsonProperty(value = "updated_at")
    LocalDateTime updatedAt;

    @Valid
    UsuarioDto usuario;
}
