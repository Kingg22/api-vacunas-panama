package io.github.kingg22.api.vacunas.panama.modules.common.dto

import io.quarkus.runtime.annotations.RegisterForReflection
import java.io.Serializable

@RegisterForReflection
@JvmRecord
data class IdNombreDto(val id: Long, val nombre: String?) : Serializable {
    @Suppress("unused")
    constructor(id: Short, nombre: String?) : this(id.toLong(), nombre)
}
