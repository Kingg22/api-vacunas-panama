package io.github.kingg22.api.vacunas.panama.modules.usuario.repository

import io.github.kingg22.api.vacunas.panama.modules.common.dto.IdNombreDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Permiso
import io.github.kingg22.api.vacunas.panama.util.withSession
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepositoryBase
import io.quarkus.panache.common.Sort
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class PermisoRepository : PanacheRepositoryBase<Permiso, Short> {
    suspend fun findAllIdNombre(): List<IdNombreDto> = withSession {
        listAll(Sort.by("id")).awaitSuspending().map {
            IdNombreDto(it.id!!, it.nombre)
        }
    }
}
