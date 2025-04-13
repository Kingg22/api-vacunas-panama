package io.github.kingg22.api.vacunas.panama.response

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.io.Serializable

/** Default implementation of [ApiError]. */
@JsonPropertyOrder(value = ["code", "property", "message"])
internal data class DefaultApiError(
    @field:NotBlank(message = "Error code cannot be blank")
    @param:NotBlank(message = "Error code cannot be blank")
    override val code: String,

    @field:Size(max = 1000, message = "Error message must be less than 1000 characters")
    @param:Size(max = 1000, message = "Error message must be less than 1000 characters")
    override val message: String,

    @field:Size(max = 255, message = "Property name must be less than 255 characters")
    @param:Size(max = 255, message = "Property name must be less than 255 characters")
    override val property: String? = null,
) : ApiError,
    Serializable {
    internal constructor(code: ApiResponseCode, property: String? = null, message: String) : this(
        code = code.name,
        message = message,
        property = property,
    )
}
