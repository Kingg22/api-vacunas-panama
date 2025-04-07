package io.github.kingg22.api.vacunas.panama.modules.pdf.controller

import io.github.kingg22.api.vacunas.panama.modules.paciente.service.PacienteService
import io.github.kingg22.api.vacunas.panama.modules.pdf.service.PdfService
import io.github.kingg22.api.vacunas.panama.modules.vacuna.service.VacunaService
import io.github.kingg22.api.vacunas.panama.response.ApiResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.sendResponse
import io.github.kingg22.api.vacunas.panama.response.DefaultApiError
import io.github.kingg22.api.vacunas.panama.util.logger
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.io.IOException
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
        @RequestParam("idPaciente") idPaciente: UUID,
        @RequestParam("idVacuna") idVacuna: UUID,
    ): ResponseEntity<ByteArray> {
        try {
            val dosisDtos = vacunaService.getDosisByIdPacienteIdVacuna(idPaciente, idVacuna)
            if (dosisDtos.isEmpty()) {
                log.debug(dosisDtos.toString())
                return ResponseEntity.noContent().build()
            }
            val pacienteDto = pacienteService.getPacienteDtoById(idPaciente)
            val idCertificado = UUID.randomUUID()
            val pdfStream = pdfService.generatePdf(pacienteDto, dosisDtos, idCertificado)

            val headers = HttpHeaders()
            headers.add(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=certificado_vacunas_$idCertificado.pdf",
            )
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)

            return ResponseEntity.ok().headers(headers).body(pdfStream)
        } catch (e: RuntimeException) {
            log.debug(e.message, e)
            return ResponseEntity.internalServerError().build()
        } catch (e: IOException) {
            log.debug(e.message, e)
            return ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping("/base64")
    fun getPdfBase64(
        @RequestParam("idPaciente") idPaciente: UUID,
        @RequestParam("idVacuna") idVacuna: UUID,
        webRequest: ServerHttpRequest,
    ): Mono<ResponseEntity<ApiResponse>> {
        val apiResponse = createResponse()
        try {
            val dosisDtos = vacunaService.getDosisByIdPacienteIdVacuna(idPaciente, idVacuna)
            if (dosisDtos.isEmpty()) {
                log.debug(dosisDtos.toString())
                apiResponse.addError(
                    DefaultApiError(
                        code = ApiResponseCode.NOT_FOUND,
                        message = "Dosis de la vacuna para el paciente no fueron encontradas para generar el PDF",
                    ),
                )
                apiResponse.addStatusCode(HttpStatus.NOT_FOUND)
                return sendResponse(apiResponse, webRequest)
            }
            val pDetalle = pacienteService.getPacienteDtoById(idPaciente)
            val idCertificado = UUID.randomUUID()
            val pdfBase64 = pdfService.generatePdfBase64(pDetalle, dosisDtos, idCertificado)
            apiResponse.addData("idCertificado", idCertificado.toString())
            apiResponse.addData("pdf", pdfBase64)
            apiResponse.addStatusCode(HttpStatus.OK)
            return sendResponse(apiResponse, webRequest)
        } catch (e: RuntimeException) {
            log.debug(e.message, e)
            apiResponse.addError(
                DefaultApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                    "Ha ocurrido un error al generar el PDF",
                ),
            )
            apiResponse.addStatusCode(HttpStatus.INTERNAL_SERVER_ERROR)
            return sendResponse(apiResponse, webRequest)
        } catch (e: IOException) {
            log.debug(e.message, e)
            apiResponse.addError(
                DefaultApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                    "Ha ocurrido un error al generar el PDF",
                ),
            )
            apiResponse.addStatusCode(HttpStatus.INTERNAL_SERVER_ERROR)
            return sendResponse(apiResponse, webRequest)
        }
    }
}
