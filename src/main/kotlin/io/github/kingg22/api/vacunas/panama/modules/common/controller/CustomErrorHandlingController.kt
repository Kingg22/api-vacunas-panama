package io.github.kingg22.api.vacunas.panama.modules.common.controller

import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createResponseEntity
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.transformApiErrorResponse
import io.github.wimdeblauwe.errorhandlingspringbootstarter.ErrorHandlingFacade
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class CustomErrorHandlingController(private val errorHandlingFacade: ErrorHandlingFacade) {
    @ExceptionHandler(RuntimeException::class, produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun handleException(exception: Throwable?, webRequest: ServerHttpRequest) =
        createResponseEntity(transformApiErrorResponse(errorHandlingFacade.handle(exception), webRequest), webRequest)
}
