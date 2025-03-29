package io.github.kingg22.api.vacunas.panama.response

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import jakarta.validation.constraints.NotNull
import org.springframework.http.HttpStatusCode
import java.io.Serializable

/** Default implementation of [ApiResponse] with a fluent builder pattern. */
@JsonPropertyOrder(value = ["status", "data", "errors", "warnings", "metadata"])
class DefaultApiResponse(
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

    override fun addStatusCode(@NotNull httpStatus: HttpStatusCode) {
        status["code"] = httpStatus
    }

    override fun addStatus(key: String, value: Serializable) {
        status[key] = value
    }

    override fun addMetadata(key: String, value: Serializable) {
        metadata[key] = value
    }

    override fun retrieveStatusCode(): Int = (status["code"] as? HttpStatusCode ?: HttpStatusCode.valueOf(500)).value()

    override fun retrieveHttpStatusCode(): HttpStatusCode =
        status["code"] as? HttpStatusCode ?: HttpStatusCode.valueOf(500)

    override fun mergeResponse(response: ApiResponse): ApiResponse {
        this.status.putAll(response.status)
        this.metadata.putAll(response.metadata)
        this.data.putAll(response.data)
        this.errors.addAll(response.errors)
        this.warnings.addAll(response.warnings)
        return this
    }

    override fun mergeContentResponse(contentResponse: ApiContentResponse): ApiContentResponse {
        this.data.putAll(contentResponse.data)
        this.errors.addAll(contentResponse.errors)
        this.warnings.addAll(contentResponse.warnings)
        return this
    }

    companion object {
        /** Creates a new builder for [DefaultApiResponse] */
        fun builder() = Builder()

        /** Create a new [ApiResponse] with DSL of [Builder] */
        fun builder(block: Builder.() -> Unit) = Builder().apply(block).build()
    }

    /** Fluent builder for creating [DefaultApiResponse] */
    data class Builder(private val response: ApiResponse = DefaultApiResponse()) {

        fun withData(key: String, value: Serializable) = apply {
            response.addData(key, value)
        }

        fun withError(error: ApiError) = apply {
            response.addError(error)
        }

        fun withErrors(errors: List<ApiError>) = apply {
            response.addErrors(errors)
        }

        fun withWarning(warning: ApiError) = apply {
            response.addWarning(warning)
        }

        fun withWarnings(warnings: List<ApiError>) = apply {
            response.addWarnings(warnings)
        }

        fun withStatusCode(httpStatus: HttpStatusCode) = apply {
            response.addStatusCode(httpStatus)
        }

        fun withStatus(key: String, value: Serializable) = apply {
            response.addStatus(key, value)
        }

        fun withMetadata(key: String, value: Serializable) = apply {
            response.addMetadata(key, value)
        }

        fun build(): ApiResponse = response
    }
}
