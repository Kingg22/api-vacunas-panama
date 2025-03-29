@file:Suppress("ktlint:standard:function-naming", "kotlin:S100")

package io.github.kingg22.api.vacunas.panama.modules.fabricante.repository

import io.github.kingg22.api.vacunas.panama.modules.fabricante.entity.Fabricante
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface FabricanteRepository : JpaRepository<Fabricante, UUID> {
    fun findByLicencia(licencia: String): Optional<Fabricante>

    fun findByUsuario_Id(idUsuario: UUID): Optional<Fabricante>
}
