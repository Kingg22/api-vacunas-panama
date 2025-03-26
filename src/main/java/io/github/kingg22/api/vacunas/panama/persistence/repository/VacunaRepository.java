package io.github.kingg22.api.vacunas.panama.persistence.repository;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Vacuna;
import io.github.kingg22.api.vacunas.panama.web.dto.VacunaFabricanteDto;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VacunaRepository extends JpaRepository<Vacuna, UUID> {
    @Query(
            "SELECT new io.github.kingg22.api.vacunas.panama.web.dto.VacunaFabricanteDto(v.id, v.nombre, f.id, f.nombre) "
                    + "FROM Vacuna v "
                    + "LEFT JOIN v.fabricantes f")
    List<VacunaFabricanteDto> findAllIdAndNombreAndFabricante();
}
