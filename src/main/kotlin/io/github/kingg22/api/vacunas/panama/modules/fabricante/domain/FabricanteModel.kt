package io.github.kingg22.api.vacunas.panama.modules.fabricante.domain

import java.time.LocalDateTime
import java.util.UUID

/**
 * Domain model representing a vaccine manufacturer.
 */
data class FabricanteModel(
    val id: UUID? = null,
    val nombre: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime? = null,
)
