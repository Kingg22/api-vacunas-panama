package io.github.kingg22.api_vacunas_panama.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.kingg22.api_vacunas_panama.persistence.entity.Persona;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link Persona}
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersonaDto implements Serializable {
    UUID id;
    @Size(max = 15)
    @Pattern(regexp = "^(PE|E|N|[23456789](?:AV|PI)?|1[0123]?(?:AV|PI)?)-(\\d{1,4})-(\\d{1,6})$",
            flags = {Pattern.Flag.CASE_INSENSITIVE, Pattern.Flag.MULTILINE},
            message = "El formato de la cédula no es válido")
    String cedula;
    @Size(max = 20)
    @Pattern(regexp = "^[A-Z0-9]{5,20}$",
            flags = {Pattern.Flag.CASE_INSENSITIVE, Pattern.Flag.MULTILINE},
            message = "El formato del pasaporte no es válido")
    String pasaporte;
    @Size(max = 100)
    String nombre;
    @Size(max = 100)
    String nombre2;
    @Size(max = 100)
    String apellido1;
    @Size(max = 100)
    String apellido2;
    @Size(max = 254)
    @Email
    String correo;
    @Size(max = 15)
    @Pattern(regexp = "^\\+\\d{1,14}$",
            flags = {Pattern.Flag.MULTILINE},
            message = "El formato del teléfono no es válido")
    String telefono;
    @PastOrPresent
    @JsonProperty(value = "fecha_nacimiento")
    LocalDateTime fechaNacimiento;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Short edad;
    Character sexo;
    @NotNull
    @Size(max = 50)
    String estado;
    @NotNull
    Boolean disabled = false;
    @Valid
    DireccionDto direccion;
    @Valid
    UsuarioDto usuario;
}
