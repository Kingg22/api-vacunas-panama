package io.github.kingg22.api.vacunas.panama.service;

import com.itextpdf.html2pdf.HtmlConverter;
import io.github.kingg22.api.vacunas.panama.modules.common.dto.EntidadDto;
import io.github.kingg22.api.vacunas.panama.modules.fabricante.dto.FabricanteDto;
import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.PacienteDto;
import io.github.kingg22.api.vacunas.panama.modules.pdf.dto.PdfDto;
import io.github.kingg22.api.vacunas.panama.modules.pdf.service.IPdfService;
import io.github.kingg22.api.vacunas.panama.modules.persona.dto.PersonaDto;
import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.DosisDto;
import jakarta.validation.constraints.NotNull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
public class PdfService implements IPdfService {
    private final ResourceLoader resourceLoader;
    private static final Logger log = LoggerFactory.getLogger(PdfService.class);

    public PdfService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public byte @org.jetbrains.annotations.NotNull [] generatePdf(
            @org.jetbrains.annotations.NotNull PacienteDto pacienteDto,
            @org.jetbrains.annotations.NotNull List<DosisDto> dosisDtos,
            @org.jetbrains.annotations.NotNull UUID idCertificate)
            throws IOException {
        return this.generatePdf(idCertificate, this.generatePdfDto(pacienteDto, dosisDtos));
    }

    @org.jetbrains.annotations.NotNull
    public String generatePdfBase64(
            @org.jetbrains.annotations.NotNull PacienteDto pacienteDto,
            @org.jetbrains.annotations.NotNull List<DosisDto> dosisDtos,
            @org.jetbrains.annotations.NotNull UUID idCertificate)
            throws IOException {
        return Base64.getEncoder()
                .encodeToString(this.generatePdf(idCertificate, this.generatePdfDto(pacienteDto, dosisDtos)));
    }

    /**
     * Generador de certificados PDF basados en una plantilla
     *
     * @param idCertificado Para colocar en caché el certificado
     * @param pdfDto DTO para añadir información al HTML
     * @return byte[] con el PDF
     * @throws IOException Al buscar una imagen en classpath resources
     */
    private byte @org.jetbrains.annotations.NotNull [] generatePdf(UUID idCertificado, PdfDto pdfDto)
            throws IOException {
        log.debug("Generando PDF con ID: {}", idCertificado);
        String template = generateHtmlTemplate(idCertificado, pdfDto);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(template, outputStream);
        this.saveCertificadoCache(idCertificado, outputStream.toString());
        return outputStream.toByteArray();
    }

    @org.jetbrains.annotations.NotNull
    private PdfDto generatePdfDto(
            @org.jetbrains.annotations.NotNull @NotNull PacienteDto pacienteDto, List<DosisDto> dosisDtos) {

        String identificacion = obtenerIdentificacion(pacienteDto);
        PersonaDto personaDto = pacienteDto.persona();
        String nombres = String.join(
                        " ",
                        Optional.ofNullable(personaDto.nombre()).orElse(""),
                        Optional.ofNullable(personaDto.nombre2()).orElse(""))
                .trim();

        String apellidos = String.join(
                        " ",
                        Optional.ofNullable(personaDto.apellido1()).orElse(""),
                        Optional.ofNullable(personaDto.apellido2()).orElse(""))
                .trim();

        log.debug("Received a request to generate PDF with Paciente DTOs");
        log.debug("Dosis a agregar: {}", dosisDtos);
        log.debug("Paciente ID: {}", personaDto.id());
        log.debug("Identificación a colocar: {}", identificacion);

        PdfDto pdfDto = new PdfDto(
                nombres,
                apellidos,
                identificacion,
                Optional.ofNullable(personaDto.fechaNacimiento())
                        .map(LocalDateTime::toLocalDate)
                        .orElse(null),
                personaDto.id(),
                dosisDtos);

        log.debug(pdfDto.toString());
        return pdfDto;
    }

    /** Obtiene la identificación del paciente en el orden de prioridad correcto. */
    private String obtenerIdentificacion(@org.jetbrains.annotations.NotNull PacienteDto pacienteDto) {
        return Optional.ofNullable(pacienteDto.persona().cedula())
                .filter(id -> !id.isBlank())
                .or(() -> Optional.ofNullable(pacienteDto.persona().pasaporte()))
                .or(() -> Optional.ofNullable(pacienteDto.identificacionTemporal()))
                .orElseGet(() -> pacienteDto.persona().id().toString());
    }

    private void saveCertificadoCache(@org.jetbrains.annotations.NotNull @NotNull UUID idCertificate, String file) {
        /* Temporal do nothing */
    }

    private String getIconImageBase64() throws IOException {
        Resource resource = resourceLoader.getResource(ResourceLoader.CLASSPATH_URL_PREFIX.concat("images/icon.png"));
        byte[] imageBytes = Files.readAllBytes(Paths.get(resource.getURI()));
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    @org.jetbrains.annotations.NotNull
    private String generateHtmlTemplate(
            @org.jetbrains.annotations.NotNull @NotNull UUID certificateId,
            @org.jetbrains.annotations.NotNull @NotNull PdfDto pdfDto)
            throws IOException {
        StringBuilder dosisRows = new StringBuilder();
        String base64Image = "data:image/png;base64,".concat(this.getIconImageBase64());
        String template =
                """
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
                    <div class="title">CERTIFICADO DIGITAL DE VACUNAS PANAMA</div>
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
                .replace("{{identificacion}}", pdfDto.identificacion())
                .replace("{{certificate_id}}", certificateId.toString());
        template = pdfDto.fechaNacimiento() != null
                ? template.replace(
                        "{{fecha_nacimiento}}", pdfDto.fechaNacimiento().toString())
                : template.replace("{{fecha_nacimiento}}", "N/A");

        // Se agrega de forma dinámica todas las dosis encontradas a la tabla HTML
        pdfDto.dosis().forEach(dosisDto -> {
            String fabricantes = dosisDto.vacuna().fabricantes().stream()
                    .map(FabricanteDto::entidad)
                    .map(EntidadDto::nombre)
                    .collect(Collectors.joining(", ", "N/A", ""));

            dosisRows.append(String.format(
                    "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
                    dosisDto.numeroDosis(),
                    dosisDto.vacuna().nombre(),
                    fabricantes,
                    dosisDto.fechaAplicacion(),
                    dosisDto.sede().entidad().nombre()));
        });
        return template.replace("{{dosis}}", dosisRows.toString());
    }
}
