package io.github.kingg22.api.vacunas.panama.modules.fabricante.repository

import io.github.kingg22.api.vacunas.panama.modules.fabricante.entity.Fabricante
import io.github.kingg22.api.vacunas.panama.util.withSession
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepositoryBase
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class FabricanteRepository : PanacheRepositoryBase<Fabricante, UUID> {
    suspend fun findByIdOrNull(id: UUID): Fabricante? = withSession { findById(id).awaitSuspending() }

    suspend fun findByLicencia(licencia: String): Fabricante? = withSession {
        find("licencia = ?1", licencia).firstResult().awaitSuspending()
    }

    suspend fun findByUsuarioId(idUsuario: UUID): Fabricante? = withSession {
        find("usuario.id = ?1", idUsuario).firstResult().awaitSuspending()
    }
}
