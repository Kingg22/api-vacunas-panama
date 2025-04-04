package io.github.kingg22.api.vacunas.panama.modules.vacuna.controller

import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.InsertDosisDto
import io.github.kingg22.api.vacunas.panama.modules.vacuna.service.IVacunaService
import io.github.kingg22.api.vacunas.panama.response.ApiResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createAndSendResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.sendResponse
import io.github.kingg22.api.vacunas.panama.util.toArrayList
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping(path = ["/vaccines"], produces = [MediaType.APPLICATION_JSON_VALUE])
class VacunaController(private val vacunaService: IVacunaService) {
    @GetMapping
    fun getVacunas(request: ServerHttpRequest) =
        createAndSendResponse(request, "vacunas", vacunaService.vacunasFabricante.toArrayList())

    @PostMapping("/create-dosis")
    fun createDosis(
        @RequestBody @Valid insertDosisDto: InsertDosisDto,
        servletWebRequest: ServerHttpRequest,
    ): Mono<ResponseEntity<ApiResponse>> {
        val apiResponse = createResponse()
        apiResponse.mergeContentResponse(vacunaService.createDosis(insertDosisDto))
        if (apiResponse.hasErrors()) {
            apiResponse.addStatusCode(HttpStatus.BAD_REQUEST)
        } else {
            apiResponse.addStatusCode(HttpStatus.CREATED)
        }
        return sendResponse(apiResponse, servletWebRequest)
    }
}
