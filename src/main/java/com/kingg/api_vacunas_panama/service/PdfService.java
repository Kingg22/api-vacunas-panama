package com.kingg.api_vacunas_panama.service;

import com.itextpdf.html2pdf.HtmlConverter;
import com.kingg.api_vacunas_panama.web.dto.DosisDto;
import com.kingg.api_vacunas_panama.web.dto.PdfDto;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
public class PdfService {

    /**
     * Generate a PDF as a byte array
     */
    public byte[] generatePdf(PdfDto pdfDto) {
        String template = generateHtmlTemplate(pdfDto);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(template, outputStream);
        return outputStream.toByteArray();
    }

    public String generatePdfBase64(PdfDto pdfDto) {
        return Base64.getEncoder().encodeToString(generatePdf(pdfDto));
    }

    private String generateHtmlTemplate(@NotNull PdfDto pdfDto) {
        StringBuilder dosisRows = new StringBuilder();
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
                    </style>
                </head>
                <body>
                <div class="section">
                    <div class="title">PANAMA DIGITAL VACCINES CERTIFICATE</div>
                    <div class="title">CERTIFICADO DIGITAL DE VACUNAS PANAMÁ</div>
                    <p>Esquema completo de vacunación</p>
                    <p>Complete vaccination scheme</p>
                </div>
                
                <div class="section">
                    <div class="subtitle">Apellidos y nombre:</div>
                    <span>{{apellidos}}, {{nombres}}</span><br/>
                    <div class="subtitle">Fecha de nacimiento:</div>
                    <span>{{fecha_nacimiento}}</span>
                </div>
                
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
        template = template.replace("{{nombres}}", pdfDto.nombres())
                .replace("{{apellidos}}", pdfDto.apellidos())
                .replace("{{fecha_nacimiento}}", pdfDto.fechaNacimiento().toString());

        // Se agrega de forma dinámica todas las dosis encontradas a la tabla
        for (DosisDto dosisDto : pdfDto.dosis()) {
            dosisRows.append("<tr>")
                    .append("<td>").append(dosisDto.numeroDosis()).append("</td>")
                    .append("<td>").append(dosisDto.vacuna().nombre()).append("</td>")
                    .append("<td>").append(dosisDto.vacuna().fabricantes().isEmpty() ? "N/A" : dosisDto.vacuna().fabricantes().iterator().next().getNombre()).append("</td>")
                    .append("<td>").append(dosisDto.fechaAplicacion()).append("</td>")
                    .append("<td>").append(dosisDto.sede().getNombre()).append("</td>")
                    .append("</tr>");
        }

        template = template.replace("{{dosis}}", dosisRows.toString());
        return template;
    }

}
