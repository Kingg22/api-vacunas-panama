package io.github.kingg22.api.vacunas.panama.persistence.repository

import io.github.kingg22.api.vacunas.panama.persistence.entity.Permiso
import io.github.kingg22.api.vacunas.panama.web.dto.IdNombreDto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PermisoRepository : JpaRepository<Permiso, Short> {
    @Query("SELECT new io.github.kingg22.api.vacunas.panama.web.dto.IdNombreDto(p.id, p.nombre) FROM Permiso p")
    fun findAllIdNombre(): List<IdNombreDto>
}
