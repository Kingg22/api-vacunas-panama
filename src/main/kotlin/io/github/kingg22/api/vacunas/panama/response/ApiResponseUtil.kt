package io.github.kingg22.api.vacunas.panama.response

import io.github.kingg22.api.vacunas.panama.util.logger
import io.github.wimdeblauwe.errorhandlingspringbootstarter.ApiErrorResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.context.request.ServletWebRequest
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
     * @param servletWebRequest [ServletWebRequest] request data used to extract information.
     */
    @JvmStatic
    fun setMetadata(apiResponse: ApiResponse, servletWebRequest: ServletWebRequest) {
        log.debug("servletWebRequest: {}", servletWebRequest)
        log.debug("locale: {}", servletWebRequest.locale)
        apiResponse.apply {
            addMetadata("path", servletWebRequest.request.requestURI)
            addMetadata("timestamp", Instant.now().toString())
        }
    }

    /**
     * Creates a standardized HTTP response from the API response.
     *
     * @param apiResponse [ApiResponse] object containing the response with status.
     * @param webRequest [ServletWebRequest] used to set metadata in the response.
     * @return A [ResponseEntity] with the status code and body set to the API response object.
     */
    @JvmStatic
    fun sendResponse(apiResponse: ApiResponse, webRequest: ServletWebRequest): ResponseEntity<ApiResponse> =
        apiResponse.apply {
            setMetadata(this, webRequest)
            log.debug(toString())
        }.let {
            ResponseEntity.status(it.retrieveHttpStatusCode()).body(it)
        }

    /**
     * Transforms an [ApiErrorResponse] from the error-handling library into a custom API response format.
     * Additionally, it hides internal errors in the response.
     *
     * @param apiErrorResponse [ApiErrorResponse] object to be transformed.
     * @param request Additional request information, indicating the endpoint where the exception was thrown.
     * @return A new [ApiResponse] object.
     */
    @JvmStatic
    fun transformApiErrorResponse(apiErrorResponse: ApiErrorResponse, request: Any): ApiResponse =
        DefaultApiResponse.builder {
            withStatusCode(apiErrorResponse.httpStatus)

            val errorMessage = if (
                apiErrorResponse.message.contains("Dto") ||
                apiErrorResponse.message.contains("api.vacunas.panama")
            ) {
                "Intente nuevamente"
            } else {
                apiErrorResponse.message
            }

            withError(DefaultApiError(code = apiErrorResponse.code, property = null, message = errorMessage))

            apiErrorResponse.fieldErrors.forEach {
                withError(
                    DefaultApiError.builder {
                        withCode(it.code)
                        withMessage(it.message)
                        withProperties(it.property)
                    },
                )
            }

            apiErrorResponse.globalErrors.forEach {
                withError(DefaultApiError(code = it.code, message = it.message))
            }

            apiErrorResponse.parameterErrors.forEach {
                withError(DefaultApiError(code = it.code, property = it.parameter, message = it.message))
            }

            when (request) {
                is ServletWebRequest -> setMetadata(this.build(), request)
                is HttpServletRequest -> setMetadata(this.build(), ServletWebRequest(request))
                else -> withMetadata("timestamp", Instant.now().toString())
            }

            log.debug(
                "ErrorResponse(code: {}, message: {}, properties: {})",
                apiErrorResponse.code,
                apiErrorResponse.message,
                apiErrorResponse.properties,
            )
        }
}
