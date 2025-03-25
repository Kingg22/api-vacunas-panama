package io.github.kingg22.api_vacunas_panama.persistence.repository;

import io.github.kingg22.api_vacunas_panama.persistence.entity.Distrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DistritoRepository extends JpaRepository<Distrito, Short> {

    Optional<Distrito> findDistritoByNombre(String nombre);

    Optional<Distrito> findDistritoByNombreAndProvincia_Id(String nombre, Short idProvincia);

    Optional<Distrito> findDistritoByNombreAndProvincia_Nombre(String nombre, String nombreProvincia);

}
