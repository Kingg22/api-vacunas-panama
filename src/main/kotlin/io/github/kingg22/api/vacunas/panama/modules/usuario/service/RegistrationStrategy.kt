package io.github.kingg22.api.vacunas.panama.modules.usuario.service

import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RegisterUserDto
import io.github.kingg22.api.vacunas.panama.response.ApiContentResponse

interface RegistrationStrategy {
    suspend fun validate(registerUserDto: RegisterUserDto): RegistrationResult
    suspend fun create(registerUserDto: RegisterUserDto): ApiContentResponse
}
