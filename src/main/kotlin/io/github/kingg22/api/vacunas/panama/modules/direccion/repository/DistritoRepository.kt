package io.github.kingg22.api.vacunas.panama.modules.direccion.repository

import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Distrito
import io.github.kingg22.api.vacunas.panama.util.withSession
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepositoryBase
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class DistritoRepository : PanacheRepositoryBase<Distrito, Short> {
    suspend fun findByIdOrNull(id: Short): Distrito? = withSession { findById(id).awaitSuspending() }
    suspend fun listAllDistritos() = withSession { listAll().awaitSuspending() }
}
