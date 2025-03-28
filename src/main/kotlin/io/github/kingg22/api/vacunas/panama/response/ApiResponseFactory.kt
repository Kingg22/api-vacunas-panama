package io.github.kingg22.api.vacunas.panama.response

import org.jetbrains.annotations.Contract

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
    @JvmStatic
    @Contract(" -> new")
    fun createResponse(): ApiResponse = DefaultApiResponse()

    /**
     * Creates a new [ApiContentResponse] instance ready for population
     *
     * @return A [DefaultApiResponse] for fluent response construction
     */
    @JvmStatic
    @Contract(" -> new")
    fun createContentResponse(): ApiContentResponse = DefaultApiResponse()
}
