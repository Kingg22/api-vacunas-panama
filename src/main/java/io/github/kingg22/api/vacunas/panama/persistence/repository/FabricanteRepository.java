package io.github.kingg22.api.vacunas.panama.persistence.repository;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Fabricante;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FabricanteRepository extends JpaRepository<Fabricante, UUID> {

    Optional<Fabricante> findByLicencia(String licencia);

    Optional<Fabricante> findByUsuario_Id(UUID idUsuario);
}
