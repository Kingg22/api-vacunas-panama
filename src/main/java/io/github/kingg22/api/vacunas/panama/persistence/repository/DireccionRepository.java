package io.github.kingg22.api.vacunas.panama.persistence.repository;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Direccion;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DireccionRepository extends JpaRepository<Direccion, UUID> {

    Optional<List<Direccion>> findDireccionByDireccionAndDistrito_Id(String direccion, int idDistrito);

    Optional<List<Direccion>> findDireccionByDireccionStartingWith(String direccion);

    Optional<List<Direccion>> findDireccionByDireccionAndDistrito_Nombre(String direccion, String distrito);
}
