package io.github.kingg22.api.vacunas.panama.modules.common.controller

import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createResponseBuilder
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createResponseEntity
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider

@Provider
class CustomErrorHandlingController : ExceptionMapper<RuntimeException> {
    override fun toResponse(exception: RuntimeException?) = createResponseEntity(
        createResponseBuilder {
            // TODO
            withStatusCode(500)
            withData(mapOf("message" to "Internal Server Error"))
        },
    )
}
