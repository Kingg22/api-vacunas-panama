package io.github.kingg22.api.vacunas.panama.response

/**
 * This extension function for the ApiResponse class checks the object for errors.
 *
 * If the number of errors is equal to or greater than the value specified in the minErrors parameter (the default is 1),
 * it returns the object itself. Otherwise, it returns null.
 *
 * Example:
 * ```kotlin
 * fun controller(): ApiResponse {
 *     response.returnIfErrors()?.let { return it }
 * }
 * ```
 *
 * @param minErrors Number of errors to evaluate
 * @return [ApiResponse] if the condition is true otherwise null
 */
fun ApiResponse.returnIfErrors(minErrors: Int = 1): ApiResponse? = if (errors.size >= minErrors) this else null

/**
 * Convert an [ApiResponse] to [ApiResponseBuilder].
 * @return [ApiResponseBuilder] with all content of actual [ApiResponse]
 */
fun ApiResponse.asBuilder() = ApiResponseBuilder(this)

/**
 * Allows you to construct or modify an [ApiResponse] object using DSL style.
 * @return [ApiResponse] with all.
 */
fun ApiResponse.builder(block: ApiResponseBuilder.() -> Unit) = ApiResponseBuilder(this).apply(block).build()
