package io.github.kingg22.api_vacunas_panama.persistence.repository;

import io.github.kingg22.api_vacunas_panama.persistence.entity.Permiso;
import io.github.kingg22.api_vacunas_panama.web.dto.IdNombreDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PermisoRepository extends JpaRepository<Permiso, Short> {

    Optional<Permiso> findByNombreOrId(String nombre, Short id);

    @Query("SELECT new com.kingg.api_vacunas_panama.web.dto.IdNombreDto(p.id, p.nombre) FROM Permiso p")
    List<IdNombreDto> findAllIdNombre();

}
