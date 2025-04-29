package io.github.kingg22.api.vacunas.panama.modules.usuario.domain

import java.time.LocalDateTime
import java.util.UUID

/**
 * Domain model representing a user in the system.
 * This is a pure Kotlin data class with immutable properties.
 */
data class UsuarioModel(
    val id: UUID? = null,
    val username: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime? = null,
    val disabled: Boolean = true,
)
