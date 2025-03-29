package io.github.kingg22.api.vacunas.panama.modules.persona.service

import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona
import jakarta.validation.constraints.NotNull
import java.util.Optional
import java.util.UUID

interface IPersonaService {
    fun getPersona(identifier: @NotNull String): Optional<Persona>

    fun getPersonaByUserID(idUser: @NotNull UUID): Optional<Persona>
}
