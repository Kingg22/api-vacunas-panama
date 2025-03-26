package io.github.kingg22.api.vacunas.panama.web.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.PastOrPresent
import java.time.LocalDate
import java.util.UUID

/** DTO for generate PDF with basic information  */
@JvmRecord
data class PdfDto @JvmOverloads constructor(
    @NotBlank val nombres: String,
    @NotBlank val apellidos: String,
    @NotBlank val identificacion: String,
    @PastOrPresent val fechaNacimiento: LocalDate? = null,
    val id: UUID? = null,
    // Extraer id fecha_aplicacion, numero_dosis, vacuna.nombre, sede.nombre, doctor.nombre,
    // doctor.idoneidad, lote
    // De esos datos si es null colocar 'Desconocido'
    @NotEmpty @Valid val dosis: List<DosisDto> = emptyList(),
)
