package io.github.kingg22.api_vacunas_panama.persistence.repository;

import io.github.kingg22.api_vacunas_panama.persistence.entity.Usuario;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByUsername(String username);

    @Query("SELECT p.usuario " +
            "FROM Persona p " +
            "WHERE (:cedula IS NOT NULL OR :pasaporte IS NOT NULL OR :correo IS NOT NULL) AND " +
            "(:cedula IS NULL OR p.cedula = :cedula) AND " +
            "(:pasaporte IS NULL OR p.pasaporte = :pasaporte) AND " +
            "(:correo IS NULL OR p.correo = :correo)")
    Optional<Usuario> findByCedulaOrPasaporteOrCorreo(@Param("cedula") String cedula, @Param("pasaporte") String pasaporte, @Param("correo") String correo);

    @Query("SELECT f.usuario FROM Fabricante f " +
            "LEFT JOIN Entidad e ON f.id = e.id " +
            "WHERE (:licencia IS NOT NULL OR :correo IS NOT NULL) AND " +
            "(:licencia IS NULL OR f.licencia = :licencia) AND " +
            "(:correo IS NULL OR f.correo = :correo)")
    Optional<Usuario> findByLicenciaOrCorreo(@Param("licencia") String licencia, @Param("correo") String correo);

    @Query("SELECT p.usuario FROM Persona p WHERE p.cedula = :cedula")
    Optional<Usuario> findByCedula(@NotNull @Param("cedula") String cedula);

    @Query("SELECT p.usuario FROM Persona p WHERE p.correo = :correo")
    Optional<Usuario> findByCorreoPersona(@NotNull @Param("correo") String correo);

    @Query("SELECT f.usuario FROM Fabricante f WHERE f.correo = :correo")
    Optional<Usuario> findByCorreoFabricante(@NotNull @Param("correo") String correo);

    @Query("SELECT f.usuario FROM Fabricante f WHERE f.licencia = :licencia")
    Optional<Usuario> findByLicencia(@NotNull @Param("licencia") String licencia);

}
