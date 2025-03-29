package io.github.kingg22.api.vacunas.panama.modules.common.dto

import java.io.Serializable
import java.util.UUID

@JvmRecord
data class UUIDNombreDto(val id: UUID, val nombre: String) : Serializable
