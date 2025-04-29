@file:Suppress("ktlint:standard:function-naming", "kotlin:S100")

package io.github.kingg22.api.vacunas.panama.modules.direccion.repository

import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DireccionRepository : JpaRepository<Direccion, UUID> {
    fun findDireccionByDescripcionAndDistrito_Id(descripcion: String? = null, idDistrito: Short): List<Direccion>

    fun findDireccionByDescripcionStartingWith(descripcion: String? = null): List<Direccion>

    fun findDireccionByDescripcionAndDistrito_Nombre(descripcion: String, distrito: String?): List<Direccion>
}
