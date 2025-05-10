package io.github.kingg22.api.vacunas.panama.modules.persona.entity

import io.github.kingg22.api.vacunas.panama.modules.persona.dto.PersonaDto
import io.github.kingg22.api.vacunas.panama.modules.persona.dto.fromPersona

fun Persona.toPersonaDto() = PersonaDto.fromPersona(this)
