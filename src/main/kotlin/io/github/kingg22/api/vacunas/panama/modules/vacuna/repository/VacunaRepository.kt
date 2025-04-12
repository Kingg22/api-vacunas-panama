package io.github.kingg22.api.vacunas.panama.modules.vacuna.repository

import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.VacunaFabricanteDto
import io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.Vacuna
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface VacunaRepository : JpaRepository<Vacuna, UUID> {
    @Query(
        "SELECT new io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.VacunaFabricanteDto(" +
            "v.id, v.nombre, f.id, f.nombre) " +
            "FROM Vacuna v " +
            "LEFT JOIN v.fabricantes f " +
            "ORDER BY v.nombre",
    )
    fun findAllIdAndNombreAndFabricante(): List<VacunaFabricanteDto>
}
