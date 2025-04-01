package io.github.kingg22.api.vacunas.panama.response

import io.github.kingg22.api.vacunas.panama.util.logger
import io.github.wimdeblauwe.errorhandlingspringbootstarter.ApiErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import reactor.core.publisher.Mono
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
     * @param serverHttpRequest [ServerHttpRequest] request data used to extract information.
     */
    @JvmStatic
    fun setMetadata(apiResponse: ApiResponse, serverHttpRequest: ServerHttpRequest) {
        apiResponse.apply {
            log.debug("servletWebRequest: {}", serverHttpRequest.toString())
            addMetadata("path", serverHttpRequest.uri.path)
            addMetadata("timestamp", Instant.now().toString())
        }
    }

    /**
     * Creates a standardized HTTP response from the API response.
     *
     * @param apiResponse [ApiResponse] object containing the response with status.
     * @param serverHttpRequest [ServerHttpRequest] used to set metadata in the response.
     * @return A [ResponseEntity] with the status code and body set to the API response object.
     */
    @JvmStatic
    fun sendResponse(
        apiResponse: ApiResponse,
        serverHttpRequest: ServerHttpRequest,
    ): Mono<ResponseEntity<ApiResponse>> = apiResponse.apply {
        setMetadata(this, serverHttpRequest)
        log.debug(toString())
    }.let {
        Mono.just(ResponseEntity.status(it.retrieveHttpStatusCode()).body(it))
    }

    @JvmStatic
    fun createAndSendResponse(
        request: ServerHttpRequest,
        attributeName: String,
        data: Serializable,
        statusCode: HttpStatusCode = HttpStatus.OK,
    ): Mono<ResponseEntity<ApiResponse>> = sendResponse(
        ApiResponseFactory.createResponseBuilder {
            withData(attributeName, data)
            withStatusCode(statusCode)
        },
        request,
    )

    /**
     * Transforms an [ApiErrorResponse] from the error-handling library into a custom API response format.
     * Additionally, it hides internal errors in the response.
     *
     * @param apiErrorResponse [ApiErrorResponse] object to be transformed.
     * @param request Additional request information, indicating the endpoint where the exception was thrown.
     * @return A new [ApiResponse] object.
     */
    @JvmStatic
    fun transformApiErrorResponse(apiErrorResponse: ApiErrorResponse, request: ServerHttpRequest): ApiResponse =
        ApiResponseFactory.createResponseBuilder {
            withStatusCode(apiErrorResponse.httpStatus)

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

            setMetadata(build(), request)

            log.debug(
                "ErrorResponse(code: {}, message: {}, properties: {})",
                apiErrorResponse.code,
                apiErrorResponse.message,
                apiErrorResponse.properties,
            )
        }
}
