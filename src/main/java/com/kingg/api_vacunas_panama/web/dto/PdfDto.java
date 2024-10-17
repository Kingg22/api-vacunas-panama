package com.kingg.api_vacunas_panama.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * DTO for generate PDF with basic information
 */
public record PdfDto(
        @NotNull @NotBlank
        String nombres,
        @NotNull @NotBlank
        String apellidos,
        @NotNull @NotBlank
        String identificacion,
        @NotNull
        UUID id,
        @NotNull @NotEmpty
        // Extraer id fecha_aplicacion, numero_dosis, vacuna.nombre, sede.nombre, doctor.nombre, doctor.idoneidad, lote
        // De esos datos si es null colocar 'Desconocido'
        @Valid List<DosisDto> dosis
) {
}
