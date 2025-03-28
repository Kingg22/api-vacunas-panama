package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.web.dto.DosisDto;
import io.github.kingg22.api.vacunas.panama.web.dto.PacienteDto;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface IPdfService {
    byte[] generatePdf(PacienteDto pacienteDto, List<DosisDto> dosisDtos, UUID idCertificate) throws IOException;

    String generatePdfBase64(PacienteDto pacienteDto, List<DosisDto> dosisDtos, UUID idCertificate) throws IOException;
}
