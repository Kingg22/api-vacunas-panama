package io.github.kingg22.api.vacunas.panama.modules.direccion.repository

import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion
import io.github.kingg22.api.vacunas.panama.util.withSession
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepositoryBase
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class DireccionRepository : PanacheRepositoryBase<Direccion, UUID> {
    suspend fun findByIdOrNull(id: UUID): Direccion? = withSession { findById(id).awaitSuspending() }

    suspend fun findDireccionByDescripcionAndDistritoId(
        descripcion: String? = null,
        idDistrito: Short,
    ): List<Direccion> = withSession {
        list("descripcion = ?1 and distrito.id = ?2", descripcion, idDistrito).awaitSuspending()
    }

    suspend fun findDireccionByDescripcionStartingWith(descripcion: String? = null): List<Direccion> = withSession {
        list("descripcion LIKE ?1", "$descripcion%").awaitSuspending()
    }

    suspend fun findDireccionByDescripcionAndDistritoNombre(descripcion: String, distrito: String?): List<Direccion> =
        withSession { list("descripcion = ?1 AND distrito.nombre = ?2", descripcion, distrito).awaitSuspending() }
}
