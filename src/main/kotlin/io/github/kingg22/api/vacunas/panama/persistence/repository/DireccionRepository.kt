@file:Suppress("ktlint:standard:function-naming", "kotlin:S100")

package io.github.kingg22.api.vacunas.panama.persistence.repository

import io.github.kingg22.api.vacunas.panama.persistence.entity.Direccion
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface DireccionRepository : JpaRepository<Direccion, UUID> {
    fun findDireccionByDireccionAndDistrito_Id(direccion: String?, idDistrito: Int): Optional<List<Direccion>>

    fun findDireccionByDireccionStartingWith(direccion: String?): Optional<List<Direccion>>

    fun findDireccionByDireccionAndDistrito_Nombre(direccion: String, distrito: String?): Optional<List<Direccion>>
}
