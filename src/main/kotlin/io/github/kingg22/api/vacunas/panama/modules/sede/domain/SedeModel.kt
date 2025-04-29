package io.github.kingg22.api.vacunas.panama.modules.sede.domain

import io.github.kingg22.api.vacunas.panama.modules.direccion.domain.DireccionModel
import java.time.LocalDateTime
import java.util.UUID

/**
 * Domain model representing a sede (location) in the system.
 * This is a pure Kotlin data class with immutable properties.
 */
data class SedeModel(
    val id: UUID? = null,
    val nombre: String,
    val dependencia: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime? = null,
    val direccion: DireccionModel? = null,
)
