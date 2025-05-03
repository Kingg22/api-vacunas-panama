package io.github.kingg22.api.vacunas.panama.modules.direccion.repository

import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Provincia
import io.github.kingg22.api.vacunas.panama.util.withSession
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepositoryBase
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class ProvinciaRepository : PanacheRepositoryBase<Provincia, Short> {
    suspend fun listAllProvincias() = withSession { listAll().awaitSuspending() }
}
