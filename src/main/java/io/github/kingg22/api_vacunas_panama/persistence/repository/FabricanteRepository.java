package io.github.kingg22.api_vacunas_panama.persistence.repository;

import io.github.kingg22.api_vacunas_panama.persistence.entity.Fabricante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FabricanteRepository extends JpaRepository<Fabricante, UUID> {

    Optional<Fabricante> findByLicencia(String licencia);

    Optional<Fabricante> findByUsuario_Id(UUID idUsuario);

}
