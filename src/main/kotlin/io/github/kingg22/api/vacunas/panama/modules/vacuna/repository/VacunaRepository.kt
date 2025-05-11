package io.github.kingg22.api.vacunas.panama.modules.vacuna.repository

import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.VacunaFabricanteDto
import io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.Vacuna
import io.github.kingg22.api.vacunas.panama.util.withSession
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepositoryBase
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class VacunaRepository : PanacheRepositoryBase<Vacuna, UUID> {
    suspend fun findByIdOrNull(id: UUID): Vacuna? = withSession { findById(id).awaitSuspending() }
    suspend fun findAllIdAndNombreAndFabricante(): List<VacunaFabricanteDto> = withSession { _ ->
        list(
            """
        SELECT DISTINCT v
        FROM Vacuna v
        LEFT JOIN v.fabricantes f
        LEFT JOIN f.entidad e
        ORDER BY v.id
        """,
        ).awaitSuspending().map {
            val fabricante = it.fabricantes.firstOrNull()
            VacunaFabricanteDto(it.id!!, it.nombre, fabricante?.id, fabricante?.entidad?.nombre)
        }
    }
}
