package io.github.kingg22.api.vacunas.panama.response

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import io.quarkus.runtime.annotations.RegisterForReflection
import java.io.Serializable

/** Default implementation of [ApiResponse] with a fluent builder pattern. */
@RegisterForReflection
@JsonPropertyOrder(value = ["status", "data", "errors", "warnings", "metadata"])
@JvmRecord
data class DefaultApiResponse(
    override val status: MutableMap<String, Serializable> = mutableMapOf(),
    override val data: MutableMap<String, Serializable> = mutableMapOf(),
    override val errors: MutableList<ApiError> = mutableListOf(),
    override val warnings: MutableList<ApiError> = mutableListOf(),
    override val metadata: MutableMap<String, Serializable> = mutableMapOf(),
) : ApiResponse,
    Serializable {
    override fun addData(key: String, value: Serializable) {
        data[key] = value
    }

    override fun addData(dataMap: Map<String, Serializable>) {
        data.putAll(dataMap)
    }

    override fun addError(error: ApiError) {
        errors.add(error)
    }

    override fun addErrors(errors: List<ApiError>) {
        this.errors.addAll(errors)
    }

    override fun addWarning(warning: ApiError) {
        warnings.add(warning)
    }

    override fun addWarnings(warnings: List<ApiError>) {
        this.warnings.addAll(warnings)
    }

    override fun hasErrors() = errors.isNotEmpty()

    override fun hasWarnings() = warnings.isNotEmpty()

    override fun addStatusCode(statusCode: Int) {
        status["code"] = statusCode
    }

    override fun addStatus(key: String, value: Serializable) {
        status[key] = value
    }

    override fun addMetadata(key: String, value: Serializable) {
        metadata[key] = value
    }

    override fun retrieveStatusCode(): Int = status["code"] as? Int ?: 500

    override fun mergeResponse(response: ApiResponse): ApiResponse = this.apply {
        this.status.putAll(response.status)
        this.metadata.putAll(response.metadata)
        this.data.putAll(response.data)
        this.errors.addAll(response.errors)
        this.warnings.addAll(response.warnings)
    }

    override fun mergeContentResponse(contentResponse: ApiContentResponse): ApiContentResponse = this.apply {
        this.data.putAll(contentResponse.data)
        this.errors.addAll(contentResponse.errors)
        this.warnings.addAll(contentResponse.warnings)
    }
}
