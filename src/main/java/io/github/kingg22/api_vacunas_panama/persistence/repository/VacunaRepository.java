package io.github.kingg22.api_vacunas_panama.persistence.repository;

import io.github.kingg22.api_vacunas_panama.persistence.entity.Vacuna;
import io.github.kingg22.api_vacunas_panama.web.dto.VacunaFabricanteDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VacunaRepository extends JpaRepository<Vacuna, UUID> {
    Optional<Vacuna> findByNombre(String nombre);

    @Query("SELECT new com.kingg.api_vacunas_panama.web.dto.VacunaFabricanteDto(v.id, v.nombre, f.id, f.nombre) " +
            "FROM Vacuna v " +
            "LEFT JOIN v.fabricantes f")
    List<VacunaFabricanteDto> findAllIdAndNombreAndFabricante();

}
