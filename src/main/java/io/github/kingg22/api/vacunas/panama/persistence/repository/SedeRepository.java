package io.github.kingg22.api.vacunas.panama.persistence.repository;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Sede;
import io.github.kingg22.api.vacunas.panama.web.dto.UUIDNombreDto;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SedeRepository extends JpaRepository<Sede, UUID> {

    @Query("SELECT new io.github.kingg22.api.vacunas.panama.web.dto.UUIDNombreDto(s.id, s.nombre) "
            + "FROM Sede s "
            + "WHERE s.estado LIKE 'ACTIVO'")
    List<UUIDNombreDto> findAllIdAndNombre();
}
