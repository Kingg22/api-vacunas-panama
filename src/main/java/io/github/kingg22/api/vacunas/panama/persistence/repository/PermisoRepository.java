package io.github.kingg22.api.vacunas.panama.persistence.repository;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Permiso;
import io.github.kingg22.api.vacunas.panama.web.dto.IdNombreDto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PermisoRepository extends JpaRepository<Permiso, Short> {

    Optional<Permiso> findByNombreOrId(String nombre, Short id);

    @Query("SELECT new io.github.kingg22.api.vacunas.panama.web.dto.IdNombreDto(p.id, p.nombre) FROM Permiso p")
    List<IdNombreDto> findAllIdNombre();
}
