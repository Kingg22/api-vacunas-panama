package io.github.kingg22.api.vacunas.panama.response

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.io.Serializable

/** Default implementation of [ApiError] with a fluent builder pattern. */
@JsonPropertyOrder(value = ["code", "property", "message"])
data class DefaultApiError(
    @field:NotBlank(message = "Error code cannot be blank")
    @param:NotBlank(message = "Error code cannot be blank")
    override val code: String,

    @field:Size(max = 255, message = "Property name must be less than 255 characters")
    @param:Size(max = 255, message = "Property name must be less than 255 characters")
    override val property: String? = null,

    @field:Size(max = 1000, message = "Error message must be less than 1000 characters")
    @param:Size(max = 1000, message = "Error message must be less than 1000 characters")
    override val message: String,
) : ApiError,
    Serializable {
    private constructor() : this("", null, "")
    constructor(code: ApiResponseCode, message: String) : this(
        code = code.toString(),
        message = message,
        property = null,
    )

    constructor(code: String, message: String) : this(code = code, property = null, message = message)

    constructor(code: ApiResponseCode, property: String?, message: String) : this(
        code = code.toString(),
        property = property,
        message = message,
    )

    companion object {
        /** Creates a new builder for [DefaultApiError] */
        fun builder() = Builder()

        /** Create a new [ApiError] with DSL of [Builder] */
        fun builder(block: Builder.() -> Unit) = Builder().apply(block).build()
    }

    /** Fluent builder for creating [DefaultApiError] */
    class Builder(private var error: DefaultApiError = DefaultApiError()) {

        fun withCode(code: String) = apply {
            error = error.copy(code = code)
        }

        fun withCode(apiResponseCode: ApiResponseCode) = apply {
            error = error.copy(code = apiResponseCode.toString())
        }

        fun withMessage(vararg message: String = arrayOf()) = apply {
            error = error.copy(message = message.joinToString("\n"))
        }

        fun withProperties(vararg properties: String = arrayOf()) = apply {
            error = error.copy(property = properties.joinToString())
        }

        fun fromError(nestedError: ApiError) = apply {
            error = DefaultApiError(
                code = "${error.code}, ${nestedError.code}",
                property = "${error.property}, ${nestedError.property}",
                message = "${error.message}, ${nestedError.message}",
            )
        }

        fun build(): ApiError = error
    }
}
