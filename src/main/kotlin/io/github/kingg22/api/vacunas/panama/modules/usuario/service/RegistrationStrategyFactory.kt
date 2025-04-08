package io.github.kingg22.api.vacunas.panama.modules.usuario.service

import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RegisterUserDto
import org.springframework.stereotype.Component

@Component
class RegistrationStrategyFactory(
    private val personaStrategy: PersonaRegistrationStrategy,
    private val fabricanteStrategy: FabricanteRegistrationStrategy,
) {
    fun getStrategy(dto: RegisterUserDto) = when {
        dto.cedula != null || dto.pasaporte != null -> personaStrategy
        dto.licenciaFabricante != null -> fabricanteStrategy
        else -> null
    }
}
