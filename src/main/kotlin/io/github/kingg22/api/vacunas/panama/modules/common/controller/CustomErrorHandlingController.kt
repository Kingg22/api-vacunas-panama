package io.github.kingg22.api.vacunas.panama.modules.common.controller

import io.github.kingg22.api.vacunas.panama.response.ApiResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.sendResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.transformApiErrorResponse
import io.github.wimdeblauwe.errorhandlingspringbootstarter.ErrorHandlingFacade
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import reactor.core.publisher.Mono

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
class CustomErrorHandlingController(private val errorHandlingFacade: ErrorHandlingFacade) {
    @ExceptionHandler(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun handleException(exception: Throwable?, webRequest: ServerHttpRequest): Mono<ResponseEntity<ApiResponse>> =
        sendResponse(transformApiErrorResponse(errorHandlingFacade.handle(exception), webRequest), webRequest)
}
