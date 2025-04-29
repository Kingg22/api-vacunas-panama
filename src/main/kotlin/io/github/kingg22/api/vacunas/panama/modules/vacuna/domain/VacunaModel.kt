package io.github.kingg22.api.vacunas.panama.modules.vacuna.domain

import io.github.kingg22.api.vacunas.panama.modules.fabricante.domain.FabricanteModel
import java.time.LocalDateTime
import java.util.UUID

/**
 * Domain model representing a vaccine in the system.
 * This is a pure Kotlin data class with immutable properties.
 */
data class VacunaModel(
    val id: UUID? = null,
    val nombre: String,
    val descripcion: String? = null,
    val edadMinimaDias: Short? = null,
    val fabricante: FabricanteModel? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime? = null,
)
