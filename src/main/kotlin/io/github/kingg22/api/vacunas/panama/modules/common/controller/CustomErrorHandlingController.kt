package io.github.kingg22.api.vacunas.panama.modules.common.controller

import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createResponseBuilder
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createResponseEntity
import io.github.kingg22.api.vacunas.panama.util.logger
import io.vertx.ext.web.RoutingContext
import jakarta.enterprise.inject.Default
import jakarta.inject.Inject
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider

@Provider
class CustomErrorHandlingController : ExceptionMapper<RuntimeException> {
    private val log = logger()

    @Inject
    @field:Default
    lateinit var rc: RoutingContext

    override fun toResponse(exception: RuntimeException?) = createResponseEntity(
        createResponseBuilder {
            // TODO
            log.error("Unhandled error", exception)
            withStatusCode(500)
            withData(mapOf("message" to "Internal Server Error"))
        },
        if (::rc.isInitialized) rc else null,
    )
}
