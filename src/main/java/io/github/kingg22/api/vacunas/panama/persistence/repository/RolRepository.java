package io.github.kingg22.api.vacunas.panama.persistence.repository;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Rol;
import io.github.kingg22.api.vacunas.panama.web.dto.IdNombreDto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RolRepository extends JpaRepository<Rol, Short> {

    Optional<Rol> findByNombreOrId(String nombreRol, Short id);

    @Query("SELECT new io.github.kingg22.api.vacunas.panama.web.dto.IdNombreDto(r.id, r.nombre) FROM Rol r")
    List<IdNombreDto> findAllIdNombre();
}
