package io.github.kingg22.api.vacunas.panama.response

import org.springframework.http.HttpStatusCode
import java.io.Serializable

/**
 * Fluent builder for creating [ApiResponse] and [ApiError] objects.
 * Allows chaining of methods for constructing an API response object with data, errors, warnings, status, and metadata.
 *
 * @property response The underlying [ApiResponse] object being constructed.
 */
data class ApiResponseBuilder(private val response: ApiResponse = DefaultApiResponse()) {

    /**
     * Add a single data entry to the response.
     *
     * @param key The unique identifier for the data.
     * @param value The value to be added.
     * @return The current builder instance for chaining.
     */
    fun withData(key: String, value: Serializable) = apply {
        response.addData(key, value)
    }

    /**
     * Add multiple data entries at once.
     *
     * @param dataMap A map containing the data entries.
     * @return The current builder instance for chaining.
     */
    fun withData(dataMap: Map<String, Serializable>) = apply {
        response.addData(dataMap)
    }

    /**
     * Add single or multiple errors at once.
     *
     * @param errors Vararg of error objects to add.
     * @return The current builder instance for chaining.
     */
    fun withError(vararg errors: ApiError) = apply {
        response.addErrors(errors.toList())
    }

    /**
     * Add an error using code, message, and optional property.
     *
     * @param code Error code as a string.
     * @param message Error message.
     * @param property Optional property associated with the error.
     * @return The current builder instance for chaining.
     */
    fun withError(code: String, message: String, property: String? = null) = apply {
        response.addError(DefaultApiError(code = code, property = property, message = message))
    }

    /**
     * Add an error using an [ApiResponseCode], message, and optional property.
     *
     * @param code An enum representing the response code.
     * @param message Error message.
     * @param property Optional property associated with the error.
     * @return The current builder instance for chaining.
     */
    fun withError(code: ApiResponseCode, message: String, property: String? = null) = apply {
        response.addError(DefaultApiError(code = code, property = property, message = message))
    }

    /**
     * Add an error using DSL of [ApiErrorBuilder].
     *
     * @param block DSL function with message, code and optional property.
     * @return The current builder instance for chaining.
     */
    fun withError(block: ApiErrorBuilder.() -> Unit) = apply {
        response.addError(ApiErrorBuilder().apply(block).build())
    }

    /**
     * Add a warning using DSL of [ApiErrorBuilder].
     *
     * @param block DSL function with message, code and optional property.
     * @return The current builder instance for chaining.
     */
    fun withWarning(block: ApiErrorBuilder.() -> Unit) = apply {
        response.addWarning(ApiErrorBuilder().apply(block).build())
    }

    /**
     * Add single or multiple warnings at once.
     *
     * @param warnings Vararg of warning objects to add.
     * @return The current builder instance for chaining.
     */
    fun withWarning(vararg warnings: ApiError) = apply {
        response.addWarnings(warnings.toList())
    }

    /**
     * Add a warning using code, message, and optional property.
     *
     * @param code Warning code as a string.
     * @param property Optional property associated with the warning.
     * @param message Warning message.
     * @return The current builder instance for chaining.
     */
    fun withWarning(code: String, message: String, property: String? = null) = apply {
        response.addWarning(DefaultApiError(code = code, property = property, message = message))
    }

    /**
     * Add a warning using an [ApiResponseCode], message, and optional property.
     *
     * @param code An enum representing the response code.
     * @param property Optional property associated with the warning.
     * @param message Warning message.
     * @return The current builder instance for chaining.
     */
    fun withWarning(code: ApiResponseCode, message: String, property: String? = null) = apply {
        response.addWarning(DefaultApiError(code = code, property = property, message = message))
    }

    /**
     * Add a status entry to the response.
     *
     * @param key The key representing the status.
     * @param value The value associated with the status.
     * @return The current builder instance for chaining.
     */
    fun withStatus(key: String, value: Serializable) = apply {
        response.addStatus(key, value)
    }

    /**
     * Set the HTTP status code for the response.
     *
     * @param httpStatus The HTTP status code.
     * @return The current builder instance for chaining.
     */
    fun withStatusCode(httpStatus: HttpStatusCode) = apply {
        response.addStatusCode(httpStatus)
    }

    /**
     * Add a metadata entry to the response.
     *
     * @param key The metadata key.
     * @param value The metadata value.
     * @return The current builder instance for chaining.
     */
    fun withMetadata(key: String, value: Serializable) = apply {
        response.addMetadata(key, value)
    }

    /**
     * Merge the current response with another [ApiResponse].
     * Combines data, errors, warnings, status, and metadata from both responses.
     *
     * @param otherResponse The [ApiResponse] to be merged with the current response.
     * @return The current builder instance with merged response data.
     */
    fun mergeWithResponse(otherResponse: ApiResponse) = apply {
        response.mergeResponse(otherResponse)
    }

    /**
     * Merge the current response's content with another [ApiContentResponse].
     * Combines data, errors, and warnings from both responses.
     *
     * @param otherContent The [ApiContentResponse] to be merged with the current response content.
     * @return The current builder instance with merged content data.
     */
    fun mergeWithContentResponse(otherContent: ApiContentResponse) = apply {
        response.mergeContentResponse(otherContent)
    }

    /**
     * Build the final [ApiResponse] object.
     *
     * @return The constructed [ApiResponse] object.
     */
    fun build(): ApiResponse = response

    /**
     * ApiErrorBuilder is a class designed to facilitate the construction of ApiError objects.
     *
     * The fields `message` and `code` are mandatory.
     * @throws IllegalStateException if the mandatory `message` or `code` field has not been initialized.
     */
    class ApiErrorBuilder {
        private lateinit var message: String
        private var property: String? = null
        private var code: ApiResponseCode? = null
        private var codeString: String? = null

        /**
         * Specifies the error code using an ApiResponseCode object.
         * @param code An ApiResponseCode representing the error code.
         * @return The builder itself for method chaining.
         */
        fun withCode(code: ApiResponseCode) = apply {
            this.code = code
        }

        /**
         * Specifies the error code directly as a string.
         * @param code A string representing the error code.
         * @return The builder itself for method chaining.
         */
        fun withCode(code: String) = apply {
            this.codeString = code
        }

        /**
         * Specifies the error message.
         * @param message A string describing the error.
         * @return The builder itself for method chaining.
         */
        fun withMessage(message: String) = apply {
            this.message = message
        }

        /**
         * Specifies the property associated with the error.
         * @param property The name of the property related to the error.
         * @return The builder itself for method chaining.
         */
        fun withProperty(property: String) = apply {
            this.property = property
        }

        /**
         * Builds the ApiError object, ensuring that all required fields are set.
         * @return A constructed ApiError object.
         * @throws IllegalStateException if the required `message` or `code` field is not initialized.
         */
        fun build(): ApiError {
            check(::message.isInitialized && message.isNotBlank()) { "The 'message' field is required." }
            val finalCode = code?.toString() ?: codeString
            check(!finalCode.isNullOrBlank()) { "The 'code' field is required." }

            return DefaultApiError(code = finalCode, property = property, message = message)
        }
    }
}
