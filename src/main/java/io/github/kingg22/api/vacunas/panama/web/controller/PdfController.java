package io.github.kingg22.api.vacunas.panama.web.controller;

import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode;
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory;
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil;
import io.github.kingg22.api.vacunas.panama.response.IApiResponse;
import io.github.kingg22.api.vacunas.panama.service.IPacienteService;
import io.github.kingg22.api.vacunas.panama.service.IVacunaService;
import io.github.kingg22.api.vacunas.panama.service.PdfService;
import io.github.kingg22.api.vacunas.panama.web.dto.DosisDto;
import io.github.kingg22.api.vacunas.panama.web.dto.PacienteDto;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/vacunacion/v1/pdf")
public class PdfController {
    private final PdfService pdfService;
    private final IPacienteService pacienteService;
    private final IVacunaService vacunaService;
    private final ApiResponseFactory apiResponseFactory;

    @GetMapping
    public ResponseEntity<byte[]> getPdfFile(
            @RequestParam("idPaciente") UUID idPaciente, @RequestParam("idVacuna") UUID idVacuna) {
        try {
            List<DosisDto> dosisDtos = vacunaService.getDosisByIdPacienteIdVacuna(idPaciente, idVacuna);
            if (dosisDtos.isEmpty()) {
                log.debug(dosisDtos.toString());
                return ResponseEntity.badRequest().build();
            }
            PacienteDto pacienteDto = pacienteService.getPacienteDtoById(idPaciente);
            UUID idCertificado = UUID.randomUUID();
            byte[] pdfStream = pdfService.generatePdf(pacienteDto, dosisDtos, idCertificado);

            HttpHeaders headers = new HttpHeaders();
            headers.add(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=certificado_vacunas_"
                            .concat(idCertificado.toString())
                            .concat(".pdf"));
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);

            return ResponseEntity.ok().headers(headers).body(pdfStream);
        } catch (RuntimeException | IOException e) {
            log.debug(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/base64")
    public ResponseEntity<IApiResponse<String, Serializable>> getPdfBase64(
            @RequestParam("idPaciente") UUID idPaciente,
            @RequestParam("idVacuna") UUID idVacuna,
            ServletWebRequest webRequest) {
        IApiResponse<String, Serializable> apiResponse = apiResponseFactory.createResponse();
        try {
            List<DosisDto> dosisDtos = vacunaService.getDosisByIdPacienteIdVacuna(idPaciente, idVacuna);
            if (dosisDtos.isEmpty()) {
                log.debug(dosisDtos.toString());
                apiResponse.addError(
                        ApiResponseCode.NOT_FOUND,
                        "Dosis de la vacuna para el paciente no fueron encontradas para generar el PDF");
                apiResponse.addStatusCode(HttpStatus.NOT_FOUND);
            }
            PacienteDto pDetalle = pacienteService.getPacienteDtoById(idPaciente);
            UUID idCertificado = UUID.randomUUID();
            String pdfBase64 = pdfService.generatePdfBase64(pDetalle, dosisDtos, idCertificado);
            apiResponse.addData("idCertificado", idCertificado.toString());
            apiResponse.addData("pdf", pdfBase64);
            apiResponse.addStatusCode(HttpStatus.OK);
            return ApiResponseUtil.sendResponse(apiResponse, webRequest);
        } catch (RuntimeException | IOException e) {
            log.debug(e.getMessage(), e);
            apiResponse.addError(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Ha ocurrido un error al generar el PDF");
            apiResponse.addStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return ApiResponseUtil.sendResponse(apiResponse, webRequest);
        }
    }
}
