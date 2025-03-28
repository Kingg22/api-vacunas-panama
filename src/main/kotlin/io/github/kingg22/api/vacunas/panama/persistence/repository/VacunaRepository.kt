package io.github.kingg22.api.vacunas.panama.persistence.repository

import io.github.kingg22.api.vacunas.panama.persistence.entity.Vacuna
import io.github.kingg22.api.vacunas.panama.web.dto.VacunaFabricanteDto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface VacunaRepository : JpaRepository<Vacuna, UUID> {
    @Query(
        "SELECT new io.github.kingg22.api.vacunas.panama.web.dto.VacunaFabricanteDto(v.id, v.nombre, f.id, f.nombre) " +
            "FROM Vacuna v " +
            "LEFT JOIN v.fabricantes f",
    )
    fun findAllIdAndNombreAndFabricante(): List<VacunaFabricanteDto>
}
