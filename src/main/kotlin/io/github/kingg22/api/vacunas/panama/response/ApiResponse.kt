package io.github.kingg22.api.vacunas.panama.response

import org.springframework.http.HttpStatusCode
import java.io.Serializable

/** Extended interface for API responses that include status and metadata */
interface ApiResponse : ApiContentResponse {
    /** Status information map */
    val status: MutableMap<String, Serializable>

    /** Metadata information map */
    val metadata: MutableMap<String, Serializable>

    /**
     * Add an HTTP status code to the response
     * @param httpStatus The HTTP status to be added
     */
    @Deprecated("Use addStatusCode(integer) instead", ReplaceWith("addStatusCode(httpStatus.value())"))
    fun addStatusCode(httpStatus: HttpStatusCode)

    /**
     * Add an HTTP status code to the response
     * @param statusCode The HTTP status code to be added as an integer
     */
    fun addStatusCode(statusCode: Int)

    /**
     * Add a status entry
     * @param key Status identifier
     * @param value Status value
     */
    fun addStatus(key: String, value: Serializable)

    /**
     * Add metadata entry
     * @param key Metadata identifier
     * @param value Metadata value
     */
    fun addMetadata(key: String, value: Serializable)

    /**
     * Get the HTTP status code
     * @return The HTTP status code as an integer
     */
    fun retrieveStatusCode(): Int

    /** Merge two [ApiResponse] in one */
    fun mergeResponse(response: ApiResponse): ApiResponse
}
