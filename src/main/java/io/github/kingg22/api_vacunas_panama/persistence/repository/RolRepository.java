package io.github.kingg22.api_vacunas_panama.persistence.repository;

import io.github.kingg22.api_vacunas_panama.persistence.entity.Rol;
import io.github.kingg22.api_vacunas_panama.web.dto.IdNombreDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Short> {

    Optional<Rol> findByNombreOrId(String nombreRol, Short id);

    @Query("SELECT new com.kingg.api_vacunas_panama.web.dto.IdNombreDto(r.id, r.nombre) FROM Rol r")
    List<IdNombreDto> findAllIdNombre();

}
