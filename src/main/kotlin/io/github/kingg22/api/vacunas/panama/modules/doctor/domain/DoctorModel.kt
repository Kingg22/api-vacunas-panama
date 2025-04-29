package io.github.kingg22.api.vacunas.panama.modules.doctor.domain

import io.github.kingg22.api.vacunas.panama.modules.persona.domain.PersonaModel
import java.time.LocalDateTime
import java.util.UUID

/**
 * Domain model representing a doctor in the system.
 * This is a pure Kotlin data class with immutable properties.
 */
data class DoctorModel(
    val id: UUID? = null,
    val persona: PersonaModel,
    val idoneidad: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime? = null,
)
