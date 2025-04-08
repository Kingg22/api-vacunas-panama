package io.github.kingg22.api.vacunas.panama.modules.usuario.service

import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RegisterUserDto
import io.github.kingg22.api.vacunas.panama.response.ApiContentResponse

interface RegistrationStrategy {
    fun validate(registerUserDto: RegisterUserDto): RegistrationResult
    fun create(registerUserDto: RegisterUserDto): ApiContentResponse
}
