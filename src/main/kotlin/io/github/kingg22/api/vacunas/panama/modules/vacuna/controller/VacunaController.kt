package io.github.kingg22.api.vacunas.panama.modules.vacuna.controller

import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.InsertDosisDto
import io.github.kingg22.api.vacunas.panama.modules.vacuna.service.VacunaService
import io.github.kingg22.api.vacunas.panama.response.ActualApiResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createApiAndResponseEntity
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createResponseEntity
import io.github.kingg22.api.vacunas.panama.util.toArrayList
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/vaccines"], produces = [MediaType.APPLICATION_JSON_VALUE])
class VacunaController(private val vacunaService: VacunaService) {
    @GetMapping
    suspend fun getVacunas(request: ServerHttpRequest) =
        createApiAndResponseEntity(request, mapOf("vacunas" to vacunaService.getVacunasFabricante().toArrayList()))

    @PostMapping("/create-dosis")
    suspend fun createDosis(
        @RequestBody @Valid insertDosisDto: InsertDosisDto,
        servletWebRequest: ServerHttpRequest,
    ): ResponseEntity<ActualApiResponse> {
        val apiResponse = createResponse()
        apiResponse.mergeContentResponse(vacunaService.createDosis(insertDosisDto))
        if (apiResponse.hasErrors()) {
            apiResponse.addStatusCode(400)
        } else {
            apiResponse.addStatusCode(201)
        }
        return createResponseEntity(apiResponse, servletWebRequest)
    }
}
