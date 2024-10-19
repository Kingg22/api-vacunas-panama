package com.kingg.api_vacunas_panama.service;

import com.itextpdf.html2pdf.HtmlConverter;
import com.kingg.api_vacunas_panama.web.dto.DosisDto;
import com.kingg.api_vacunas_panama.web.dto.PdfDto;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class PdfService {
    private final ResourceLoader resourceLoader;

    private String getIconImageBase64() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:images/icon.png");
        byte[] imageBytes = Files.readAllBytes(Paths.get(resource.getURI()));
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * Generate a PDF as a byte array
     */
    //Al recibir una imagen de base64 se necesita usar IOException
    public byte[] generatePdf(PdfDto pdfDto) throws IOException {
        String template = generateHtmlTemplate(pdfDto);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(template, outputStream);
        return outputStream.toByteArray();
    }

    public String generatePdfBase64(PdfDto pdfDto) throws IOException {
        return Base64.getEncoder().encodeToString(generatePdf(pdfDto));
    }

    private String generateHtmlTemplate(@NotNull PdfDto pdfDto) throws IOException {
        StringBuilder dosisRows = new StringBuilder();
        //Se convierte la imagen a base64 para remplazarla en el template
        String base64Image = "data:image/jpeg;base64," + this.getIconImageBase64();
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
                    <p>Esquema completo de vacunación</p>
                    <p>Complete vaccination scheme</p>
                </div>
                
                <img src="{{base64Image}}" alt="Logo" class="logo">
                
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
                """;// Insertar la imagen en Base64 en la plantilla HTML
                template = template.replace("{{base64Image}}", base64Image);
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
