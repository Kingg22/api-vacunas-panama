package io.github.kingg22.api.vacunas.panama.controller

import io.github.kingg22.api.vacunas.panama.response.ApiResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.sendResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.transformApiErrorResponse
import io.github.wimdeblauwe.errorhandlingspringbootstarter.ErrorHandlingFacade
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.ServletWebRequest

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice(annotations = [RestController::class])
class CustomErrorHandlingController(private val errorHandlingFacade: ErrorHandlingFacade) {
    @ExceptionHandler
    fun handleException(exception: Throwable?, webRequest: ServletWebRequest): ResponseEntity<ApiResponse> {
        val errorResponse = errorHandlingFacade.handle(exception)
        return sendResponse(transformApiErrorResponse(errorResponse, webRequest), webRequest)
    }
}
