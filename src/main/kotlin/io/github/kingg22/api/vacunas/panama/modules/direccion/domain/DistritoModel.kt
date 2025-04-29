package io.github.kingg22.api.vacunas.panama.modules.direccion.domain

/**
 * Domain model representing a district in the system.
 */
data class DistritoModel(val id: Short? = null, val nombre: String, val provincia: ProvinciaModel)
