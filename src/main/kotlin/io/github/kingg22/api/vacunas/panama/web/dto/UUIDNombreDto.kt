package io.github.kingg22.api.vacunas.panama.web.dto

import java.io.Serializable
import java.util.UUID

@JvmRecord
data class UUIDNombreDto(val id: UUID, val nombre: String) : Serializable
