package io.github.kingg22.api.vacunas.panama.modules.pdf.controller

import io.github.kingg22.api.vacunas.panama.modules.paciente.service.PacienteService
import io.github.kingg22.api.vacunas.panama.modules.pdf.service.PdfService
import io.github.kingg22.api.vacunas.panama.modules.vacuna.service.VacunaService
import io.github.kingg22.api.vacunas.panama.response.ActualApiResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createApiErrorBuilder
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createResponseEntity
import io.github.kingg22.api.vacunas.panama.util.logger
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping(path = ["/pdf"])
class PdfController(
    private val pdfService: PdfService,
    private val pacienteService: PacienteService,
    private val vacunaService: VacunaService,
) {
    private val log = logger()

    @GetMapping
    suspend fun getPdfFile(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestParam("idVacuna") idVacuna: UUID,
    ): ResponseEntity<ByteArray> {
        try {
            val personaIdString = jwt.getClaimAsString("persona")
            check(personaIdString != null) { "Persona ID is null in JWT claims with ID: ${jwt.id}" }

            val idPaciente: UUID = UUID.fromString(personaIdString)
            val dosisDtos = vacunaService.getDosisByIdPacienteIdVacuna(idPaciente, idVacuna)

            if (dosisDtos.isEmpty()) {
                log.debug(dosisDtos.toString())
                return ResponseEntity.notFound().build()
            }

            val pacienteDto = pacienteService.getPacienteDtoById(idPaciente)

            if (pacienteDto == null) {
                return ResponseEntity.notFound().build()
            }

            val idCertificado: UUID = UUID.randomUUID()
            val pdfStream = pdfService.generatePdf(pacienteDto, dosisDtos, idCertificado)

            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .headers { it: HttpHeaders ->
                    it.contentDisposition = ContentDisposition
                        .attachment()
                        .filename("certificado_vacunas_$idCertificado.pdf")
                        .build()
                }
                .body(pdfStream)
        } catch (e: Exception) {
            log.error(e.message, e)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/base64")
    suspend fun getPdfBase64(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestParam("idVacuna") idVacuna: UUID,
        webRequest: ServerHttpRequest,
    ): ResponseEntity<ActualApiResponse> {
        val apiResponse = createResponse()
        try {
            val personaIdString = jwt.getClaimAsString("persona")
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
                apiResponse.addStatusCode(HttpStatus.NOT_FOUND.value())
                return createResponseEntity(apiResponse, webRequest)
            }

            val pDetalle = pacienteService.getPacienteDtoById(idPaciente)

            if (pDetalle == null) {
                apiResponse.addError(
                    createApiErrorBuilder {
                        withCode(ApiResponseCode.NOT_FOUND)
                        message = "El paciente no fue encontrado, intente nuevamente."
                    },
                )
                return createResponseEntity(apiResponse, webRequest)
            }

            val idCertificado: UUID = UUID.randomUUID()
            val pdfBase64 = pdfService.generatePdfBase64(pDetalle, dosisDtos, idCertificado)

            apiResponse.addData("id_certificado", idCertificado.toString())
            apiResponse.addData("pdf", pdfBase64)
            apiResponse.addStatusCode(HttpStatus.OK.value())
        } catch (e: Exception) {
            log.debug(e.message, e)
            apiResponse.addError(
                createApiErrorBuilder {
                    withCode(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                    message = "Ha ocurrido un error al generar el PDF"
                },
            )
            apiResponse.addStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        }
        return createResponseEntity(apiResponse, webRequest)
    }
}
