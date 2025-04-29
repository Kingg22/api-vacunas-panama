package io.github.kingg22.api.vacunas.panama.modules.paciente.domain

import io.github.kingg22.api.vacunas.panama.modules.persona.domain.PersonaModel
import java.time.LocalDateTime

/**
 * Domain model representing a patient in the system.
 * This is a pure Kotlin data class with immutable properties.
 */
data class PacienteModel(
    val persona: PersonaModel,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime? = null,
    val identificacionTemporal: String? = null,
)
