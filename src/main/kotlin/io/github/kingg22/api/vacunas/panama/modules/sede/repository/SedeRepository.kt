package io.github.kingg22.api.vacunas.panama.modules.sede.repository

import io.github.kingg22.api.vacunas.panama.modules.common.dto.UUIDNombreDto
import io.github.kingg22.api.vacunas.panama.modules.sede.entity.Sede
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface SedeRepository : JpaRepository<Sede, UUID> {
    @Query(
        "SELECT new io.github.kingg22.api.vacunas.panama.modules.common.dto.UUIDNombreDto(s.id, s.nombre) " +
            "FROM Sede s " +
            "WHERE s.estado LIKE 'ACTIVO' " +
            "ORDER BY s.nombre",
    )
    fun findAllIdAndNombre(): List<UUIDNombreDto>
}
