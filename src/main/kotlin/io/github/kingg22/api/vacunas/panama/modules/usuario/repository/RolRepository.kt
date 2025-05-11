package io.github.kingg22.api.vacunas.panama.modules.usuario.repository

import io.github.kingg22.api.vacunas.panama.modules.common.dto.IdNombreDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Rol
import io.github.kingg22.api.vacunas.panama.util.withSession
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepositoryBase
import io.quarkus.panache.common.Sort
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class RolRepository : PanacheRepositoryBase<Rol, Short> {
    suspend fun findByNombreOrId(nombreRol: String?, id: Short?): Rol? = withSession {
        find(
            "SELECT r FROM Rol r WHERE r.nombre = :nombre OR r.id = :id",
            mapOf("nombre" to nombreRol, "id" to id),
        ).firstResult().awaitSuspending()
    }

    suspend fun findAllIdNombre(): List<IdNombreDto> = withSession { _ ->
        listAll(Sort.by("id")).awaitSuspending().map {
            IdNombreDto(it.id!!, it.nombre)
        }
    }
}
