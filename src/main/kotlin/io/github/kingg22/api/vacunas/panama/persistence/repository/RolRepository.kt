package io.github.kingg22.api.vacunas.panama.persistence.repository

import io.github.kingg22.api.vacunas.panama.persistence.entity.Rol
import io.github.kingg22.api.vacunas.panama.web.dto.IdNombreDto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface RolRepository : JpaRepository<Rol, Short> {
    fun findByNombreOrId(nombreRol: String?, id: Short?): Optional<Rol>

    @Query("SELECT new io.github.kingg22.api.vacunas.panama.web.dto.IdNombreDto(r.id, r.nombre) FROM Rol r")
    fun findAllIdNombre(): List<IdNombreDto>
}
