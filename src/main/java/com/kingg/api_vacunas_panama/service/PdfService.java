package com.kingg.api_vacunas_panama.service;

import com.itextpdf.html2pdf.HtmlConverter;
import com.kingg.api_vacunas_panama.web.dto.DosisDto;
import com.kingg.api_vacunas_panama.web.dto.FabricanteDto;
import com.kingg.api_vacunas_panama.web.dto.PacienteDto;
import com.kingg.api_vacunas_panama.web.dto.PdfDto;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfService {
    private final ResourceLoader resourceLoader;
    private final RedisTemplate<String, Object> redisTemplate;

    public byte[] generatePdf(PacienteDto pacienteDto, List<DosisDto> dosisDtos, UUID idCertificate) throws IOException {
        return this.generatePdf(idCertificate, this.generatePdfDto(pacienteDto, dosisDtos));
    }

    public String generatePdfBase64(PacienteDto pacienteDto, List<DosisDto> dosisDtos, UUID idCertificate) throws IOException {
        return Base64.getEncoder().encodeToString(this.generatePdf(idCertificate, this.generatePdfDto(pacienteDto, dosisDtos)));
    }

    /**
     * Generador de certificados PDF basados en una plantilla
     *
     * @param idCertificado Para colocar en caché el certificado
     * @param pdfDto        DTO para añadir información al HTML
     * @return byte[] con el PDF
     * @throws IOException Al buscar una imagen en classpath resources
     */
    private byte[] generatePdf(UUID idCertificado, PdfDto pdfDto) throws IOException {
        log.debug("Generando PDF con ID: {}", idCertificado);
        String template = generateHtmlTemplate(idCertificado, pdfDto);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(template, outputStream);
        this.saveCertificadoCache(idCertificado, outputStream.toString());
        return outputStream.toByteArray();
    }

    private PdfDto generatePdfDto(@NotNull PacienteDto pacienteDto, List<DosisDto> dosisDtos) {
        String identificacion = pacienteDto.getCedula();
        String nombres = pacienteDto.getNombre() != null ? pacienteDto.getNombre() : "";
        String apellidos = pacienteDto.getApellido1() != null ? pacienteDto.getApellido1() : "";
        if (identificacion == null || identificacion.isBlank()) {
            identificacion = pacienteDto.getPasaporte() != null ? pacienteDto.getPasaporte() : pacienteDto.getIdentificacionTemporal();
            if (identificacion == null || identificacion.isBlank()) {
                identificacion = pacienteDto.getId().toString();
            }
        }

        if (pacienteDto.getNombre2() != null && !pacienteDto.getNombre2().isBlank()) {
            if (!nombres.isBlank()) {
                nombres = nombres.concat(" ");
            }
            nombres = nombres.concat(pacienteDto.getNombre2());
        }

        if (pacienteDto.getApellido2() != null && !pacienteDto.getApellido2().isBlank()) {
            if (!apellidos.isBlank()) {
                apellidos = apellidos.concat(" ");
            }
            apellidos = apellidos.concat(pacienteDto.getApellido2());
        }

        log.debug("Received a request to generate PDF with Paciente DTOs");
        log.debug("Dosis a agregar: {}", dosisDtos.toString());
        log.debug("Paciente ID: {}", pacienteDto.getId());
        log.debug("identificación a colocar: {}", identificacion);
        PdfDto pdfDto = new PdfDto(nombres, apellidos, identificacion, pacienteDto.getFechaNacimiento().toLocalDate(), pacienteDto.getId(), dosisDtos);
        log.debug(pdfDto.toString());
        return pdfDto;
    }

    private void saveCertificadoCache(@NotNull UUID idCertificate, String file) {
        log.debug("Guardando en cache certificado por 30 días");
        redisTemplate.opsForValue().set("certificate:".concat(idCertificate.toString()), file, Duration.ofDays(30));
    }

    private String getIconImageBase64() throws IOException {
        Resource resource = resourceLoader.getResource(ResourceLoader.CLASSPATH_URL_PREFIX.concat("images/icon.png"));
        byte[] imageBytes = Files.readAllBytes(Paths.get(resource.getURI()));
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    private String generateHtmlTemplate(@NotNull UUID certificateId, @NotNull PdfDto pdfDto) throws IOException {
        StringBuilder dosisRows = new StringBuilder();
        String base64Image = "data:image/png;base64,".concat(this.getIconImageBase64());
        String template = """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <title>Certificado Vacunas Panama</title>
                    <meta charset="UTF-8">
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                        }
                
                        .title {
                            font-size: 14px;
                            font-weight: bold;
                        }
                
                        .subtitle {
                            font-size: 12px;
                            font-weight: bold;
                        }
                
                        .section {
                            margin-bottom: 10px;
                        }
                
                        .vaccination-details {
                            font-size: 12px;
                        }
                
                        table {
                            width: 100%;
                            border-collapse: collapse;
                            margin-top: 10px;
                        }
                
                        table, th, td {
                            border: 1px solid black;
                        }
                
                        th, td {
                            padding: 8px;
                            text-align: left;
                        }
                
                        .logo {
                            position: absolute;
                            top: 20px;
                            right: 20px;
                            width: 150px;
                        }
                    </style>
                </head>
                <body>
                <div class="section">
                    <div class="title">PANAMA DIGITAL VACCINES CERTIFICATE</div>
                    <div class="title">CERTIFICADO DIGITAL DE VACUNAS PANAMÁ</div>
                </div>
                
                <img src="{{base64Image}}" alt="Logo" class="logo">
                
                <div class="section">
                    <br>
                    <div class="subtitle">Apellido(s), Nombre(s):</div>
                    <div class="subtitle">Last Names(s), First Name:</div>
                    <span>{{apellidos}}, {{nombres}}</span><br/>
                    <br>
                    <div class="subtitle">Identificacion (Cédula/Pasaporte/ID temporal/ID):</div>
                    <div class="subtitle">Identification (Panamanian ID card/Passport/Temporary ID/ID):</div>
                    <span>{{identificacion}}</span><br/>
                    <br>
                    <div class="subtitle">Fecha de nacimiento:</div>
                    <div class="subtitle">Date of Birth:</div>
                    <span>{{fecha_nacimiento}}</span>
                    <br><br>
                    <div class="subtitle">ID certificado:</div>
                    <div class="subtitle">Certificate ID:</div>
                    <span>{{certificate_id}}</span>
                </div>
                <br>
                <div class="section">
                    <div class="subtitle">Datos de la vacunación / Vaccination details</div>
                    <table>
                        <thead>
                        <tr>
                            <th>Número de dosis</th>
                            <th>Nombre de vacuna</th>
                            <th>Fabricante</th>
                            <th>Fecha de vacunación</th>
                            <th>Sede de vacunación</th>
                        </tr>
                        </thead>
                        <tbody>
                        {{dosis}}
                        </tbody>
                    </table>
                </div>
                </body>
                </html>
                """;

        template = template.replace("{{base64Image}}", base64Image)
                .replace("{{nombres}}", pdfDto.nombres())
                .replace("{{apellidos}}", pdfDto.apellidos())
                .replace("{{fecha_nacimiento}}", pdfDto.fechaNacimiento().toString())
                .replace("{{identificacion}}", pdfDto.identificacion())
                .replace("{{certificate_id}}", certificateId.toString());

        // Se agrega de forma dinámica todas las dosis encontradas a la tabla HTML
        for (DosisDto dosisDto : pdfDto.dosis()) {
            String fabricantes = dosisDto.vacuna().fabricantes().isEmpty() ? "N/A" : dosisDto.vacuna().fabricantes()
                    .stream()
                    .map(FabricanteDto::getNombre)
                    .collect(Collectors.joining(", "));
            dosisRows.append("<tr>")
                    .append("<td>").append(dosisDto.numeroDosis()).append("</td>")
                    .append("<td>").append(dosisDto.vacuna().nombre()).append("</td>")
                    .append("<td>").append(fabricantes).append("</td>")
                    .append("<td>").append(dosisDto.fechaAplicacion()).append("</td>")
                    .append("<td>").append(dosisDto.sede().getNombre()).append("</td>")
                    .append("</tr>");
        }
        return template.replace("{{dosis}}", dosisRows.toString());
    }

}
