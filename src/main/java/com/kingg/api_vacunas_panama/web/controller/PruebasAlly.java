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

import java.io.IOException;
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
            // datos de PRUEBAS id Paciente = AF696D60-BC34-42BD-B525-084790EF6247
            // idVacuna = 46EC208A-4719-4897-9E3A-CD3AC176EE2E (BCG)
            // para cambiar buscar en la base de datos docker los nuevos id
            // Todos estos pasos son de pruebas, yo me encargo de hacerlo funcionar después que el PDF se crea bien
            List<DosisDto> dosisDtos = vacunaService.getDosisVacunaByIdPaciente(UUID.fromString("AF696D60-BC34-42BD-B525-084790EF6247"), idVacuna);
            if (dosisDtos.isEmpty()) {
                log.debug(dosisDtos.toString());
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            Paciente pDetalle = pacienteService.getPacienteById(UUID.fromString("AF696D60-BC34-42BD-B525-084790EF6247")).orElseThrow();
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
        } catch (RuntimeException | IOException e) {
            log.debug(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/base64")
    public ResponseEntity<String> getPdfBase64(@RequestParam("idVacuna") UUID idVacuna) {
        try {
            // datos de PRUEBAS id Paciente = AF696D60-BC34-42BD-B525-084790EF6247
            // idVacuna =  46EC208A-4719-4897-9E3A-CD3AC176EE2E
            // para cambiar buscar en la base de datos docker los nuevos id
            // Todos estos pasos son de pruebas, yo me encargo de hacerlo funcionar después que el PDF se crea bien
            List<DosisDto> dosisDtos = vacunaService.getDosisVacunaByIdPaciente(UUID.fromString("AF696D60-BC34-42BD-B525-084790EF6247"), idVacuna);
            if (dosisDtos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            Paciente pDetalle = pacienteService.getPacienteById(UUID.fromString("AF696D60-BC34-42BD-B525-084790EF6247")).orElseThrow();
            String identificacion = pDetalle.getCedula() != null ? pDetalle.getCedula() : pDetalle.getPasaporte();
            String nombres = null;
            String apellidos = null;
            // TODO hacer la concatenación de ambos datos si no es null
            log.debug(dosisDtos.toString());
            log.debug("Paciente ID: {}", pDetalle.getId());
            log.debug("identificación a colocar: {}", identificacion);
            PdfDto pdfDto = new PdfDto(pDetalle.getNombre(), pDetalle.getApellido1(), identificacion, pDetalle.getFechaNacimiento().toLocalDate(), pDetalle.getId(), dosisDtos);
            log.debug(pdfDto.toString());

            // Crear el DTO del PDF

            // Utilizar el servicio para generar el PDF en Base64
            String pdfBase64 = pdfService.generatePdfBase64(pdfDto);

            // Devolver el PDF en Base64
            return ResponseEntity.ok(pdfBase64);
        } catch (RuntimeException | IOException e) {
            log.debug(e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
