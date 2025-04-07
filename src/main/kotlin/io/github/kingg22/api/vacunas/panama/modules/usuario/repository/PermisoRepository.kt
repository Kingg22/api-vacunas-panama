package io.github.kingg22.api.vacunas.panama.modules.usuario.repository

import io.github.kingg22.api.vacunas.panama.modules.common.dto.IdNombreDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Permiso
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PermisoRepository : JpaRepository<Permiso, Short> {
    @Query(
        "SELECT new io.github.kingg22.api.vacunas.panama.modules.common.dto.IdNombreDto(p.id, p.nombre) FROM Permiso p ORDER BY p.id",
    )
    fun findAllIdNombre(): List<IdNombreDto>
}
