package io.github.kingg22.api.vacunas.panama.persistence.repository

import io.github.kingg22.api.vacunas.panama.persistence.entity.Sede
import io.github.kingg22.api.vacunas.panama.web.dto.UUIDNombreDto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface SedeRepository : JpaRepository<Sede, UUID> {
    @Query(
        "SELECT new io.github.kingg22.api.vacunas.panama.web.dto.UUIDNombreDto(s.id, s.nombre) " +
            "FROM Sede s " +
            "WHERE s.estado LIKE 'ACTIVO'",
    )
    fun findAllIdAndNombre(): List<UUIDNombreDto>
}
