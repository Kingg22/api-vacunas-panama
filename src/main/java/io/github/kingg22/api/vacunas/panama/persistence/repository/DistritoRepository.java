package io.github.kingg22.api.vacunas.panama.persistence.repository;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Distrito;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistritoRepository extends JpaRepository<Distrito, Short> {

    Optional<Distrito> findDistritoByNombre(String nombre);

    Optional<Distrito> findDistritoByNombreAndProvincia_Id(String nombre, Short idProvincia);

    Optional<Distrito> findDistritoByNombreAndProvincia_Nombre(String nombre, String nombreProvincia);
}
