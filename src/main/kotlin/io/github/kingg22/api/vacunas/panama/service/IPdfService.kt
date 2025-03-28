package io.github.kingg22.api.vacunas.panama.service

import io.github.kingg22.api.vacunas.panama.web.dto.DosisDto
import io.github.kingg22.api.vacunas.panama.web.dto.PacienteDto
import java.io.IOException
import java.util.UUID

interface IPdfService {
    @Throws(IOException::class)
    fun generatePdf(pacienteDto: PacienteDto, dosisDtos: List<DosisDto>, idCertificate: UUID): ByteArray

    @Throws(IOException::class)
    fun generatePdfBase64(pacienteDto: PacienteDto, dosisDtos: List<DosisDto>, idCertificate: UUID): String
}
