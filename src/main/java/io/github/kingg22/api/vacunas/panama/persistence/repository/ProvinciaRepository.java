package io.github.kingg22.api.vacunas.panama.persistence.repository;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Provincia;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProvinciaRepository extends JpaRepository<Provincia, Short> {

    Optional<Provincia> findByNombre(String nombre);
}
