package io.github.kingg22.api.vacunas.panama.response

import io.github.kingg22.api.vacunas.panama.util.logger
import io.vertx.ext.web.RoutingContext
import jakarta.ws.rs.core.Response
import java.io.Serializable
import java.time.Instant

/**
 * Utility class for handling and formatting API responses. Provides helper methods to standardize metadata and create
 * ResponseEntity objects.
 */
object ApiResponseUtil {
    private val log = logger()

    /**
     * Adds metadata information to the provided API response.
     *
     * @param apiResponse [ApiResponse] object to which metadata will be added.
     * @param request [RoutingContext] request data used to extract information.
     */
    @JvmStatic
    fun setMetadata(apiResponse: ApiResponse, request: RoutingContext) {
        apiResponse.apply {
            addMetadata("path", request.request().path())
            addMetadata("timestamp", Instant.now().toString())
        }
    }

    /**
     * Adds metadata information to the provided API response.
     *
     * @param apiResponseBuilder [ApiResponseBuilder] object to which metadata will be added.
     * @param request [RoutingContext] request data used to extract information.
     */
    @JvmStatic
    fun setMetadata(apiResponseBuilder: ApiResponseBuilder, request: RoutingContext) {
        apiResponseBuilder.apply {
            withMetadata("path", request.request().path())
            withMetadata("timestamp", Instant.now().toString())
        }
    }

    /**
     * Creates a standardized HTTP response from the API response.
     *
     * @param apiResponse [ApiResponse] object containing the response with status.
     * @param request [RoutingContext] used to set metadata in the response.
     * @return A [Response] with the status code and body set to [ApiResponse] the API response object.
     */
    @JvmStatic
    fun createResponseEntity(apiResponse: ApiResponse, request: RoutingContext? = null): Response = apiResponse.apply {
        if (request != null) {
            setMetadata(this, request)
        }
        log.trace(toString())
    }.let {
        Response.status(it.retrieveStatusCode()).entity(it as ActualApiResponse).build()
    }

    /**
     * Creates a standardized HTTP response from the API response and build into ResponseEntity it immediately.
     *
     * @param request [RoutingContext] used to set metadata in the response.
     * @param data [Map] shortcut to add data to the response.
     * @param statusCode [Int] HTTP status code to be set in the response. Defaults to 200 OK.
     * @param builder [ApiResponseBuilder] builder to make the response.
     * @return A [Response] with the status code and body set to [ApiResponse] the API response object.
     */
    @JvmStatic
    fun createApiAndResponseEntity(
        request: RoutingContext,
        data: Map<String, Serializable>,
        statusCode: Int = 200,
        builder: ApiResponseBuilder.() -> Unit = {},
    ) = createResponseEntity(
        ApiResponseFactory.createResponseBuilder {
            apply(builder)
            withData(data)
            withStatusCode(statusCode)
        },
        request,
    )
    /*
        /**
     * Transforms an [ApiErrorResponse] from the error-handling library into a custom API response format.
     * Additionally, it hides internal errors in the response.
     *
     * @param apiErrorResponse [ApiErrorResponse] object to be transformed.
     * @param request Additional request information, indicating the endpoint where the exception was thrown.
     * @return A new [ApiResponse] object.
     */
        @JvmStatic
        fun transformApiErrorResponse(apiErrorResponse: ApiErrorResponse, request: ServerHttpRequest) =
            ApiResponseFactory.createResponseBuilder {
                withStatusCode(apiErrorResponse.httpStatus.value())

                val errorMessage = if (
                    apiErrorResponse.message.contains("Dto") ||
                    apiErrorResponse.message.contains("api.vacunas.panama")
                ) {
                    "Intente nuevamente"
                } else {
                    apiErrorResponse.message
                }

                withError(code = apiErrorResponse.code, property = null, message = errorMessage)

                apiErrorResponse.fieldErrors.forEach {
                    withError(code = it.code, message = it.message, property = it.property)
                }

                apiErrorResponse.globalErrors.forEach {
                    withError(code = it.code, message = it.message)
                }

                apiErrorResponse.parameterErrors.forEach {
                    withError(code = it.code, property = it.parameter, message = it.message)
                }

                setMetadata(this, request)

                log.trace(
                    "ErrorResponse(code: {}, message: {}, properties: {})",
                    apiErrorResponse.code,
                    apiErrorResponse.message,
                    apiErrorResponse.properties,
                )
            }
     */
}
