package io.github.kingg22.api.vacunas.panama.modules.sede.repository

import io.github.kingg22.api.vacunas.panama.modules.common.dto.UUIDNombreDto
import io.github.kingg22.api.vacunas.panama.modules.sede.entity.Sede
import io.github.kingg22.api.vacunas.panama.util.withSession
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepositoryBase
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class SedeRepository : PanacheRepositoryBase<Sede, UUID> {
    suspend fun findByIdOrNull(id: UUID): Sede? = withSession { findById(id).awaitSuspending() }

    suspend fun findAllIdAndNombre(): List<UUIDNombreDto> = withSession { _ ->
        list(
            """
            SELECT s
            FROM Sede s
            JOIN FETCH s.entidad e
            WHERE s.entidad.estado LIKE 'ACTIVO'
            ORDER BY s.entidad.nombre
            """,
        ).awaitSuspending().map { UUIDNombreDto(it.id!!, it.entidad.nombre) }
    }
}
