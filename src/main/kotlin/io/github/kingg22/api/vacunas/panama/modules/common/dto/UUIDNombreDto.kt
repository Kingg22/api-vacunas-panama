package io.github.kingg22.api.vacunas.panama.modules.common.dto

import io.quarkus.runtime.annotations.RegisterForReflection
import java.io.Serializable
import java.util.UUID

@RegisterForReflection
@JvmRecord
data class UUIDNombreDto(val id: UUID, val nombre: String) : Serializable
