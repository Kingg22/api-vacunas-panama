package com.kingg.api_vacunas_panama.service;

import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfService {
    public ByteArrayInputStream generatePdf(String nombreCompleto, String fechaNacimiento, String identificadorCertificado, String dosis, String vacuna) throws IOException {
        String template = """
                <!DOCTYPE html>
                <html lang="es">
                               
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Certificado de Vacunación</title>
                               
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            margin: 0;
                            padding: 0;
                        }
                               
                        .container {
                            width: 800px;
                            margin: 20px auto;
                            border: 1px solid #ccc;
                            padding: 20px;
                        }
                               
                        header {
                            display: flex;
                            justify-content: space-between;
                        }
                               
                        .header-left {
                            width: 60%;
                        }
                               
                        .header-right {
                            text-align: right;
                        }
                               
                        .header-right img {
                            margin-left: 10px;
                        }
                               
                        .main {
                            display: flex;
                            justify-content: space-between;
                            margin-top: 20px;
                        }
                               
                        .personal-info {
                            width: 60%;
                        }
                               
                        .qr-code img {
                            width: 150px;
                            height: 150px;
                        }
                               
                        .vaccination-details {
                            margin-top: 20px;
                        }
                               
                        .vaccination-details h3 {
                            border-bottom: 1px solid #ccc;
                            padding-bottom: 5px;
                        }
                               
                        .vaccination-details p {
                            margin: 5px 0;
                        }
                    </style>
                               
                </head>
                               
                <body>
                    <div class="container">
                        <header>
                            <div class="header-left">
                                
                                <p>PANAMA DIGITAL COVID CERTIFICATE<br>
                                    CERTIFICADO COVID DIGITAL DE PANAMÁ</p>
                                <p><strong>Esquema completo de vacunación</strong><br>
                                    Complete vaccination scheme</p>
                            </div>
                            <div class="header-right">
                               
                              
                                <p><strong>República de Panamá</strong><br>
                                    AUTORIDAD NACIONAL PARA LA INNOVACIÓN GUBERNAMENTAL (AIG)</p>
                            </div>
                        </header>
                               
                        <section class="main">
                            <div class="personal-info">
                                <p><strong>Apellidos y nombre:</strong> [NOMBRE]</p>
                                <p><strong>Fecha de nacimiento:</strong> [FECHA_NACIMIENTO]</p>
                            </div>
                            <div class="qr-code">
                                //imagen
                            </div>
                        </section>
                               
                        <section class="vaccination-details">
                            <h3>Datos de la vacunación / Vaccination details</h3>
                            <p><strong>Identificador del certificado:</strong> [IDENTIFICADOR]</p>
                            <p><strong>Emisor del certificado:</strong> AUTORIDAD NACIONAL PARA LA INNOVACIÓN GUBERNAMENTAL (AIG)</p>
                            <p><strong>Enfermedad que se previene:</strong> COVID-19</p>
                            <p><strong>Tipo de vacuna:</strong> SARS-CoV-2 vacuna antígeno</p>
                            <p><strong>Número de dosis:</strong> [DOSIS]</p>
                            <p><strong>Vacuna administrada:</strong> [VACUNA]</p>
                            <p><strong>Fecha de vacunación:</strong> 2022-01-25</p>
                            <p><strong>Estado miembro de vacunación:</strong> PANAMÁ</p>
                        </section>
                    </div>
                </body>
                               
                </html>
                """;

        template.replace("[NOMBRE]", nombreCompleto);
        try(ByteArrayOutputStream fos = new ByteArrayOutputStream()) {
            HtmlConverter.convertToPdf(template, fos);

            return new ByteArrayInputStream(fos.toByteArray());
        }
    }
}
