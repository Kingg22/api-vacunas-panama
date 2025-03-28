package io.github.kingg22.api.vacunas.panama.response

import java.io.Serializable

/**
 * Represents an error in the API response with a standardized structure.
 * Provides flexibility for different error types while maintaining a consistent interface.
 */
interface ApiError : Serializable {
    /** Unique error code identifying the type of error */
    val code: String

    /** Optional property associated with the error */
    val property: String?

    /** Descriptive error message */
    val message: String?
}
