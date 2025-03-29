package io.github.kingg22.api.vacunas.panama.modules.pdf.service

import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.PacienteDto
import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.DosisDto
import java.io.IOException
import java.util.UUID

interface IPdfService {
    @Throws(IOException::class)
    fun generatePdf(pacienteDto: PacienteDto, dosisDtos: List<DosisDto>, idCertificate: UUID): ByteArray

    @Throws(IOException::class)
    fun generatePdfBase64(pacienteDto: PacienteDto, dosisDtos: List<DosisDto>, idCertificate: UUID): String
}
