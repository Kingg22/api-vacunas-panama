package io.github.kingg22.api.vacunas.panama.web.dto

import java.io.Serializable

@JvmRecord
data class IdNombreDto(val id: Long, val nombre: String?) : Serializable {
    constructor(id: Short, nombre: String?) : this(id.toLong(), nombre)
    constructor(id: Int, nombre: String?) : this(id.toLong(), nombre)
}
