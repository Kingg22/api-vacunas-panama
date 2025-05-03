package io.github.kingg22.api.vacunas.panama.modules.pdf.controller

import io.github.kingg22.api.vacunas.panama.modules.paciente.service.PacienteService
import io.github.kingg22.api.vacunas.panama.modules.pdf.service.PdfService
import io.github.kingg22.api.vacunas.panama.modules.vacuna.service.VacunaService
import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createApiErrorBuilder
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createResponseEntity
import io.github.kingg22.api.vacunas.panama.util.logger
import io.vertx.ext.web.RoutingContext
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Response
import java.util.UUID

@Path("/pdf")
class PdfController(
    private val pdfService: PdfService,
    private val pacienteService: PacienteService,
    private val vacunaService: VacunaService,
) {
    private val log = logger()

    @GET
    suspend fun getPdfFile(@QueryParam("idVacuna") idVacuna: UUID): Response {
        try {
            val jwt = object {
                val id: String = "1"
            }
            val personaIdString: String? = null
            check(personaIdString != null) { "Persona ID is null in JWT claims with ID: ${jwt.id}" }

            val idPaciente: UUID = UUID.fromString(personaIdString)
            val dosisDtos = vacunaService.getDosisByIdPacienteIdVacuna(idPaciente, idVacuna)

            if (dosisDtos.isEmpty()) {
                log.debug(dosisDtos.toString())
                return Response.status(404).build()
            }

            val pacienteDto = pacienteService.getPacienteDtoById(idPaciente)

            if (pacienteDto == null) {
                return Response.status(404).build()
            }

            val idCertificado: UUID = UUID.randomUUID()
            val pdfStream = pdfService.generatePdf(pacienteDto, dosisDtos, idCertificado)

            return Response.ok(pdfStream)
                .type("application/pdf")
                .header("Content-Disposition", "attachment; filename=\"certificado_vacunas_$idCertificado.pdf\"")
                .build()
        } catch (e: Exception) {
            log.error(e.message, e)
            return Response.status(500).build()
        }
    }

    @Path("/base64")
    @GET
    suspend fun getPdfBase64(@QueryParam("idVacuna") idVacuna: UUID, rc: RoutingContext): Response {
        val apiResponse = createResponse()
        try {
            // TODO add authentication principal
            val jwt = object {
                val id: String = "2"
            }
            val personaIdString: String? = null
            check(personaIdString != null) { "Persona ID is null in JWT claims with ID: ${jwt.id}" }

            val idPaciente: UUID = UUID.fromString(personaIdString)
            val dosisDtos = vacunaService.getDosisByIdPacienteIdVacuna(idPaciente, idVacuna)

            if (dosisDtos.isEmpty()) {
                log.debug(dosisDtos.toString())
                apiResponse.addError(
                    createApiErrorBuilder {
                        withCode(ApiResponseCode.NOT_FOUND)
                        message = "Dosis de la vacuna para el paciente no fueron encontradas para generar el PDF"
                    },
                )
                apiResponse.addStatusCode(404)
                return createResponseEntity(apiResponse, rc)
            }

            val pDetalle = pacienteService.getPacienteDtoById(idPaciente)

            if (pDetalle == null) {
                apiResponse.addError(
                    createApiErrorBuilder {
                        withCode(ApiResponseCode.NOT_FOUND)
                        message = "El paciente no fue encontrado, intente nuevamente."
                    },
                )
                return createResponseEntity(apiResponse, rc)
            }

            val idCertificado: UUID = UUID.randomUUID()
            val pdfBase64 = pdfService.generatePdfBase64(pDetalle, dosisDtos, idCertificado)

            apiResponse.addData("id_certificado", idCertificado.toString())
            apiResponse.addData("pdf", pdfBase64)
            apiResponse.addStatusCode(200)
        } catch (e: Exception) {
            log.debug(e.message, e)
            apiResponse.addError(
                createApiErrorBuilder {
                    withCode(ApiResponseCode.INTERNAL_SERVER_ERROR)
                    message = "Ha ocurrido un error al generar el PDF"
                },
            )
            apiResponse.addStatusCode(500)
        }
        return createResponseEntity(apiResponse, rc)
    }
}
