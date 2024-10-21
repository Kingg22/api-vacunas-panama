package com.kingg.api_vacunas_panama.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record RegisterUser(@Valid @NotNull UsuarioDto usuario,
                           @Nullable
                           @Size(max = 15)
                           @Pattern(regexp = "^(PE|E|N|[23456789](?:AV|PI)?|1[0123]?(?:AV|PI)?)-(\\d{1,4})-(\\d{1,6})$",
                                   flags = {Pattern.Flag.CASE_INSENSITIVE, Pattern.Flag.MULTILINE},
                                   message = "El formato de la cédula no es válido")
                           @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
                           String cedula,
                           @Nullable
                           @Size(max = 20)
                           @Pattern(regexp = "^[A-Z0-9]{5,20}$",
                                   flags = {Pattern.Flag.CASE_INSENSITIVE, Pattern.Flag.MULTILINE},
                                   message = "El formato del pasaporte no es válido")
                           @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
                           String pasaporte,
                           @Nullable
                           @Size(max = 50)
                           @Pattern(regexp = "^.+/DNFD$",
                                   flags = {Pattern.Flag.CASE_INSENSITIVE, Pattern.Flag.MULTILINE},
                                   message = "La licenciaFabricante no es válida")
                           @JsonProperty(value = "licencia_fabricante", access = JsonProperty.Access.WRITE_ONLY)
                           String licenciaFabricante) implements Serializable {
}
