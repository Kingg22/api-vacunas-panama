package io.github.kingg22.api.vacunas.panama.modules.pdf.service

import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.PacienteDto
import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.DosisDto
import java.util.UUID

/**
 * Service interface for generating PDF certificates related to vaccination records.
 * Provides methods for both raw PDF generation and base64-encoded output.
 */
interface PdfService {

    /**
     * Generates a PDF certificate based on patient and dose information.
     *
     * @param pacienteDto DTO containing patient details.
     * @param dosisDtos List of DTOs representing applied doses.
     * @param idCertificate UUID of the certificate being generated (for tracking or registry).
     * @return A [ByteArray] representing the raw PDF content.
     */
    fun generatePdf(pacienteDto: PacienteDto, dosisDtos: List<DosisDto>, idCertificate: UUID): ByteArray

    /**
     * Generates a PDF certificate and encodes it to a Base64 string.
     * Useful for sending over APIs or embedding in documents/QRs.
     *
     * @param pacienteDto DTO containing patient details.
     * @param dosisDtos List of DTOs representing applied doses.
     * @param idCertificate UUID of the certificate being generated.
     * @return A [String] representing the PDF content encoded in Base64.
     */
    fun generatePdfBase64(pacienteDto: PacienteDto, dosisDtos: List<DosisDto>, idCertificate: UUID): String
}
