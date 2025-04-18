package io.github.kingg22.api.vacunas.panama.modules.pdf.controller

import io.github.kingg22.api.vacunas.panama.modules.paciente.service.PacienteService
import io.github.kingg22.api.vacunas.panama.modules.pdf.service.PdfService
import io.github.kingg22.api.vacunas.panama.modules.vacuna.service.VacunaService
import io.github.kingg22.api.vacunas.panama.response.ApiResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createApiErrorBuilder
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.sendResponse
import io.github.kingg22.api.vacunas.panama.util.logger
import org.springframework.http.ContentDisposition
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
import reactor.core.publisher.Mono
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
    fun getPdfFile(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestParam("idVacuna") idVacuna: UUID,
    ): Mono<ResponseEntity<ByteArray>> {
        try {
            val personaIdString = jwt.getClaimAsString("persona")
            check(personaIdString != null) { "Persona ID is null in JWT claims with ID: ${jwt.id}" }

            val idPaciente = UUID.fromString(personaIdString)
            val dosisDtos = vacunaService.getDosisByIdPacienteIdVacuna(idPaciente, idVacuna)

            if (dosisDtos.isEmpty()) {
                log.debug(dosisDtos.toString())
                return Mono.just(ResponseEntity.notFound().build())
            }

            val pacienteDto = pacienteService.getPacienteDtoById(idPaciente)
            val idCertificado = UUID.randomUUID()
            val pdfStream = pdfService.generatePdf(pacienteDto, dosisDtos, idCertificado)

            return Mono.just(
                ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .headers {
                        it.contentDisposition = ContentDisposition
                            .attachment()
                            .filename("certificado_vacunas_$idCertificado.pdf")
                            .build()
                    }
                    .body(pdfStream),
            )
        } catch (e: Exception) {
            log.error(e.message, e)
            return Mono.just(ResponseEntity.internalServerError().build())
        }
    }

    @GetMapping("/base64")
    fun getPdfBase64(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestParam("idVacuna") idVacuna: UUID,
        webRequest: ServerHttpRequest,
    ): Mono<ResponseEntity<ApiResponse>> {
        val apiResponse = createResponse()
        try {
            val idPaciente = UUID.fromString(jwt.getClaimAsString("persona"))
            val dosisDtos = vacunaService.getDosisByIdPacienteIdVacuna(idPaciente, idVacuna)

            if (dosisDtos.isEmpty()) {
                log.debug(dosisDtos.toString())
                apiResponse.addError(
                    createApiErrorBuilder {
                        withCode(ApiResponseCode.NOT_FOUND)
                        message = "Dosis de la vacuna para el paciente no fueron encontradas para generar el PDF"
                    },
                )
                apiResponse.addStatusCode(HttpStatus.NOT_FOUND)
                return sendResponse(apiResponse, webRequest)
            }

            val pDetalle = pacienteService.getPacienteDtoById(idPaciente)
            val idCertificado = UUID.randomUUID()
            val pdfBase64 = pdfService.generatePdfBase64(pDetalle, dosisDtos, idCertificado)

            apiResponse.addData("id_certificado", idCertificado.toString())
            apiResponse.addData("pdf", pdfBase64)
            apiResponse.addStatusCode(HttpStatus.OK)

            return sendResponse(apiResponse, webRequest)
        } catch (e: Exception) {
            log.debug(e.message, e)
            apiResponse.addError(
                createApiErrorBuilder {
                    withCode(HttpStatus.INTERNAL_SERVER_ERROR)
                    message = "Ha ocurrido un error al generar el PDF"
                },
            )
            apiResponse.addStatusCode(HttpStatus.INTERNAL_SERVER_ERROR)
            return sendResponse(apiResponse, webRequest)
        }
    }
}
