@file:Suppress("ktlint:standard:function-naming", "kotlin:S100")

package io.github.kingg22.api.vacunas.panama.modules.direccion.repository

import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DireccionRepository : JpaRepository<Direccion, UUID> {
    fun findDireccionByDireccionAndDistrito_Id(direccion: String?, idDistrito: Int): List<Direccion>?

    fun findDireccionByDireccionStartingWith(direccion: String?): List<Direccion>?

    fun findDireccionByDireccionAndDistrito_Nombre(direccion: String, distrito: String?): List<Direccion>?
}
