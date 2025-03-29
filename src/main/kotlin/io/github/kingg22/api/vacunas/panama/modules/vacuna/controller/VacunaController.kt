package io.github.kingg22.api.vacunas.panama.modules.vacuna.controller

import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.InsertDosisDto
import io.github.kingg22.api.vacunas.panama.modules.vacuna.service.IVacunaService
import io.github.kingg22.api.vacunas.panama.response.ApiResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.sendResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.ServletWebRequest

@RestController
@RequestMapping(path = ["/vacunacion/v1/vaccines/"], produces = [MediaType.APPLICATION_JSON_VALUE])
class VacunaController(private val vacunaService: IVacunaService) {
    @PostMapping("/create-dosis")
    fun createDosis(
        @RequestBody @Valid insertDosisDto: InsertDosisDto,
        servletWebRequest: ServletWebRequest,
    ): ResponseEntity<ApiResponse> {
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
