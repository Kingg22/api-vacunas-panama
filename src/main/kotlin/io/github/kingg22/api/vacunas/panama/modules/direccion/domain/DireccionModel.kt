package io.github.kingg22.api.vacunas.panama.modules.direccion.domain

import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DireccionDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.ProvinciaDto
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
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
) {
    companion object {
        val DEFAULT_DIRECCION_MODEL = DireccionModel(
            descripcion = DireccionDto.DEFAULT_DIRECCION,
            distrito = DistritoModel(
                0,
                "Por registrar",
                ProvinciaModel(0, ProvinciaDto.DEFAULT_PROVINCIA),
            ),
            createdAt = LocalDateTime.now(UTC),
        )
    }
}
