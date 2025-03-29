package io.github.kingg22.api.vacunas.panama.modules.pdf.dto

import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.DosisDto
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.PastOrPresent
import java.time.LocalDate
import java.util.UUID

/** DTO for generate PDF with basic information  */
@JvmRecord
data class PdfDto @JvmOverloads constructor(
    @field:NotBlank @param:NotBlank val nombres: String,

    @field:NotBlank @param:NotBlank val apellidos: String,

    @field:NotBlank @param:NotBlank val identificacion: String,

    @field:PastOrPresent @param:PastOrPresent val fechaNacimiento: LocalDate? = null,

    val id: UUID? = null,
    // Extraer id fecha_aplicacion, numero_dosis, vacuna.nombre, sede.nombre, doctor.nombre,
    // doctor.idoneidad, lote
    // De esos datos si es null colocar 'Desconocido'
    @field:NotEmpty @param:NotEmpty @field:Valid @param:Valid val dosis: List<DosisDto> = emptyList(),
)
