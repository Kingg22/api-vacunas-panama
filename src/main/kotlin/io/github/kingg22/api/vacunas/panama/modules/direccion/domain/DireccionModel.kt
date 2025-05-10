package io.github.kingg22.api.vacunas.panama.modules.direccion.domain

import java.time.LocalDateTime
import java.util.UUID

/**
 * Domain model representing an address in the system.
 * This is a pure Kotlin data class with immutable properties.
 */
data class DireccionModel(
    val id: UUID? = null,
    val descripcion: String,
    val distrito: DistritoModel,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime? = null,
)
