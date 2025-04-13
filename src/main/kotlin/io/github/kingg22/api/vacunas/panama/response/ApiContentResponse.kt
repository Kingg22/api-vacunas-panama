package io.github.kingg22.api.vacunas.panama.response

import java.io.Serializable

/** Base interface for API content responses, providing methods to add data, errors, and warnings flexibly. */
interface ApiContentResponse : Serializable {
    /** Retrieve the current data map */
    val data: MutableMap<String, Serializable>

    /** Retrieve the current errors list */
    val errors: MutableList<ApiError>

    /** Retrieve the current warnings list */
    val warnings: MutableList<ApiError>

    /**
     * Add a single data entry to the response
     * @param key Unique identifier for the data
     * @param value Serializable value to be added
     */
    fun addData(key: String, value: Serializable)

    /**
     * Add multiple data entries at once
     * @param dataMap Map of data entries to add
     */
    fun addData(dataMap: Map<String, Serializable>)

    /**
     * Add an error to the response
     * @param error The error to be added
     */
    fun addError(error: ApiError)

    /**
     * Add a list of errors to the response
     * @param errors The list of errors to be added
     */
    fun addErrors(errors: List<ApiError>)

    /**
     * Add a warning to the response
     * @param warning The warning to be added
     */
    fun addWarning(warning: ApiError)

    /**
     * Add a list of warnings to the response
     * @param warnings The list of warnings to be added
     */
    fun addWarnings(warnings: List<ApiError>)

    /** Check if any errors exist */
    fun hasErrors(): Boolean

    /** Check if any warnings exist */
    fun hasWarnings(): Boolean

    /** Merge two [ApiContentResponse] in one */
    fun mergeContentResponse(contentResponse: ApiContentResponse): ApiContentResponse

    operator fun plus(other: ApiContentResponse): ApiContentResponse = mergeContentResponse(other)

    operator fun plusAssign(other: ApiContentResponse) {
        mergeContentResponse(other)
    }
}
