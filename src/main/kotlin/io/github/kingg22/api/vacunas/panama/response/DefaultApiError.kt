package io.github.kingg22.api.vacunas.panama.response

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import io.quarkus.runtime.annotations.RegisterForReflection
import java.io.Serializable

/** Default implementation of [ApiError]. */
@RegisterForReflection
@JsonPropertyOrder(value = ["code", "property", "message"])
@JvmRecord
data class DefaultApiError(
    override val code: String,
    override val message: String,
    override val property: String? = null,
) : ApiError,
    Serializable {
    /** Convenience constructor for creating an [DefaultApiError] with a [ApiResponseCode] and a message. */
    constructor(code: ApiResponseCode, property: String? = null, message: String) : this(
        code = code.name,
        message = message,
        property = property,
    )
}
