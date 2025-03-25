package io.github.kingg22.api_vacunas_panama.persistence.repository;

import io.github.kingg22.api_vacunas_panama.persistence.entity.Persona;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PersonaRepository extends JpaRepository<Persona, UUID> {

    @Query("SELECT p " +
            "FROM Persona p " +
            "WHERE (:cedula IS NOT NULL OR :pasaporte IS NOT NULL OR :correo IS NOT NULL) AND" +
            "(:cedula IS NULL OR p.cedula = :cedula) AND " +
            "(:pasaporte IS NULL OR p.pasaporte = :pasaporte) AND " +
            "(:correo IS NULL OR p.correo = :correo)")
    Optional<Persona> findByCedulaOrPasaporteOrCorreo(@Param("cedula") String cedula, @Param("pasaporte") String pasaporte, @Param("correo") String correo);

    Optional<Persona> findByUsuario_Id(@NotNull UUID id);

    Optional<Persona> findByUsuario_Username(@NotNull String username);
}
