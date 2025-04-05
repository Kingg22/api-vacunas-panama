package io.github.kingg22.api.vacunas.panama.modules.usuario.repository

import io.github.kingg22.api.vacunas.panama.modules.common.dto.IdNombreDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Rol
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface RolRepository : JpaRepository<Rol, Short> {
    fun findByNombreOrId(nombreRol: String?, id: Short?): Optional<Rol>

    @Query(
        "SELECT new io.github.kingg22.api.vacunas.panama.modules.common.dto.IdNombreDto(r.id, r.nombre) FROM Rol r ORDER BY r.id",
    )
    fun findAllIdNombre(): List<IdNombreDto>
}
