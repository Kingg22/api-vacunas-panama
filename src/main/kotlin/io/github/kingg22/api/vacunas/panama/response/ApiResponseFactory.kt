package io.github.kingg22.api.vacunas.panama.response

import org.jetbrains.annotations.Contract
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Lazy

/**
 * Factory for creating standardized API responses.
 *
 * This factory provides a centralized way to create API responses with consistent structure.
 * It supports dependency injection and can be easily extended or mocked for testing.
 *
 * Key features:
 * - Creates default API responses
 * - Supports easy response creation in various application layers
 * - Provides a single point of response generation
 */
object ApiResponseFactory {
    /**
     * Creates a new API response instance
     *
     * @return A new [DefaultApiResponse] instance ready for population
     */
    @Lazy
    @Bean
    @JvmStatic
    @Contract(" -> new")
    fun createResponse(): ApiResponse = DefaultApiResponse()

    /**
     * Creates a new [ApiContentResponse] instance ready for population
     *
     * @return A [DefaultApiResponse] for fluent response construction
     */
    @Lazy
    @Bean
    @JvmStatic
    @Contract(" -> new")
    fun createContentResponse(): ApiContentResponse = DefaultApiResponse()

    /** Creates a new builder for [ApiResponse] */
    @Lazy
    @Bean
    @JvmStatic
    @Contract(" -> new")
    fun createResponseBuilder(response: ApiResponse = DefaultApiResponse()) = ApiResponseBuilder(response)

    /** Creates a new builder for [ApiError] */
    @Lazy
    @Bean
    @JvmStatic
    @Contract(" -> new")
    fun createApiErrorBuilder() = ApiResponseBuilder.ApiErrorBuilder()

    /** Create a new [ApiResponse] with DSL of [ApiResponseBuilder] */
    @JvmStatic
    @Contract(" -> new")
    fun createResponseBuilder(response: ApiResponse = DefaultApiResponse(), block: ApiResponseBuilder.() -> Unit) =
        ApiResponseBuilder(response).apply(block).build()

    /** Create a new [ApiError] with DSL of [ApiResponseBuilder.ApiErrorBuilder] */
    @JvmStatic
    @Contract(" -> new")
    fun createApiErrorBuilder(block: ApiResponseBuilder.ApiErrorBuilder.() -> Unit) =
        ApiResponseBuilder.ApiErrorBuilder().apply(block).build()
}
