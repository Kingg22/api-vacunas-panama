package com.kingg.api_vacunas_panama.web.controller;

import com.kingg.api_vacunas_panama.persistence.entity.Paciente;
import com.kingg.api_vacunas_panama.service.PacienteService;
import com.kingg.api_vacunas_panama.service.PdfService;
import com.kingg.api_vacunas_panama.service.VacunaService;
import com.kingg.api_vacunas_panama.web.dto.DosisDto;
import com.kingg.api_vacunas_panama.web.dto.PdfDto;
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

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/vacunacion/v1/pdf")
public class PruebasAlly {
    private final PdfService pdfService;
    private final PacienteService pacienteService;
    private final VacunaService vacunaService;

    // endpoint completo = http://localhost:8080/vacunacion/v1/pdf?idVacuna=....
    @GetMapping
    public ResponseEntity<byte[]> getPdfFile(@RequestParam("idVacuna") UUID idVacuna) {
        try {
            // datos de PRUEBAS id Paciente = 4E40CB68-E567-4EBC-9669-75FAB2FDF933 (pasaporte MN876543C Tomas Gonzales)
            // idVacuna = 7123E92C-EF0B-4001-8004-83578F932B4A (Hep A adultos)
            // para cambiar buscar en la base de datos docker los nuevos id
            // Todos estos pasos son de pruebas, yo me encargo de hacerlo funcionar después que el PDF se crea bien
            List<DosisDto> dosisDtos = vacunaService.getDosisVacunaByIdPaciente(UUID.fromString("4E40CB68-E567-4EBC-9669-75FAB2FDF933"), idVacuna);
            if (dosisDtos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            Paciente pDetalle = pacienteService.getPacienteById(UUID.fromString("4E40CB68-E567-4EBC-9669-75FAB2FDF933")).orElseThrow();
            String identificacion = pDetalle.getCedula() != null ? pDetalle.getCedula() : pDetalle.getPasaporte();
            String nombres = null;
            String apellidos = null;
            // TODO hacer la concatenación de ambos datos si no es null
            log.debug(dosisDtos.toString());
            log.debug("Paciente ID: {}", pDetalle.getId());
            log.debug("identificación a colocar: {}", identificacion);
            PdfDto pdfDto = new PdfDto(pDetalle.getNombre(), pDetalle.getApellido1(), identificacion, pDetalle.getFechaNacimiento().toLocalDate(), pDetalle.getId(), dosisDtos);
            log.debug(pdfDto.toString());
            // utilizando el dto anterior se genera el pdf
            byte[] pdfStream = pdfService.generatePdf(pdfDto);

            // Devolverlo como archivo
            // al pdf anterior se le coloca un id para identificarlo (luego YO REY lo guardo en cache)
            UUID idCertificado = UUID.randomUUID();
            // Configurar los encabezados HTTP para enviar el archivo
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vacuna_".concat(idCertificado.toString()).concat(".pdf"));
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);

            // Retornar el archivo como respuesta
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfStream);
        } catch (RuntimeException e) {
            log.debug(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

/*    // después que funcione como archivo probar para de devolverlo como base64
    // http://localhost:8080/vacunacion/v1/pdf/base64?idVacuna=....
    @GetMapping("/base64")
    public ResponseEntity<IApiResponse<String, Serializable>> getPdfBase64(@RequestParam("idVacuna") UUID idVacuna, ServletWebRequest request) {
        // uso de Rey para darle formato
        IApiResponse<String, Serializable> apiResponse = new ApiResponse();
        try {
            List<DosisDto> dosisDtos = vacunaService.getDosisVacunaByIdPaciente(UUID.fromString("4E40CB68-E567-4EBC-9669-75FAB2FDF933"), idVacuna);
            Paciente pDetalle = pacienteService.getPacienteById(UUID.fromString("4E40CB68-E567-4EBC-9669-75FAB2FDF933")).orElseThrow();
            String identificacion = pDetalle.getCedula() != null ? pDetalle.getCedula() : pDetalle.getPasaporte();
            PdfDto pdfDto = new PdfDto(pDetalle.getNombre().concat(pDetalle.getNombre2()), pDetalle.getApellido1().concat(pDetalle.getNombre2()), identificacion, pDetalle.getFechaNacimiento().toLocalDate(), pDetalle.getId(), dosisDtos);
            // utilizando el dto anterior haces el pdf
            // Generar el PDF y devolverlo como base64
            UUID idCertificado = UUID.randomUUID();
            apiResponse.addData("pdf", pdfBase64);
            apiResponse.addStatusCode(HttpStatus.OK);
        } catch (RuntimeException runtimeException) {
            log.error(runtimeException.getMessage(), runtimeException);
            apiResponse.addStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ApiResponseUtil.sendResponse(apiResponse, request);
    }*/

}
