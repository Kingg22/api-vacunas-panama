package io.github.kingg22.api.vacunas.panama.modules.usuario.service

import io.github.kingg22.api.vacunas.panama.response.ApiError

sealed class RegistrationResult {
    class RegistrationSuccess(val outcome: Any) : RegistrationResult()
    class RegistrationError(val errors: List<ApiError>) : RegistrationResult() {
        constructor(error: ApiError) : this(listOf(error))
    }
}
