package io.github.kingg22.api.vacunas.panama.persistence.repository;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Paciente;
import io.github.kingg22.api.vacunas.panama.web.dto.ViewPacienteVacunaEnfermedadDto;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PacienteRepository extends JpaRepository<Paciente, UUID> {

    @Query(name = "Paciente.getVacunaEnfermedades", nativeQuery = true)
    List<ViewPacienteVacunaEnfermedadDto> findAllFromViewVacunaEnfermedad(@Param("id") UUID id);

    @Query(name = "Paciente.getVacunaPaciente", nativeQuery = true)
    List<ViewPacienteVacunaEnfermedadDto> findAllFromViewVacunaEnfermedad(
            @Param("id") UUID id, @Param("vacuna") UUID vacuna);

    @Query("SELECT p " + "FROM Paciente p "
            + "WHERE (:cedula IS NOT NULL OR :pasaporte IS NOT NULL OR :idTemporal IS NOT NULL OR :correo IS NOT NULL OR :username IS NOT NULL) AND"
            + "(:cedula IS NOT NULL AND p.cedula = :cedula) AND "
            + "(:pasaporte IS NOT NULL AND p.pasaporte = :pasaporte) AND "
            + "(:idTemporal IS NOT NULL AND p.identificacionTemporal = :idTemporal) AND "
            + "(:correo IS NOT NULL AND p.correo = :correo) AND "
            + "(:username IS NOT NULL AND p.usuario.username = :username)")
    Optional<Paciente> findByCedulaOrPasaporteOrIdTemporalOrCorreoOrUsername(
            @Param("cedula") String cedula,
            @Param("pasaporte") String pasaporte,
            @Param("idTemporal") String idTemporal,
            @Param("correo") String correo,
            @Param("username") String username);

    Optional<Paciente> findByIdentificacionTemporal(String idTemporal);

    Optional<Paciente> findByUsuario_Id(@NotNull UUID idUser);

    Optional<Paciente> findByCedula(String cedula);

    Optional<Paciente> findByPasaporte(String pasaporte);

    Optional<Paciente> findByCorreo(String correo);

    Optional<Paciente> findByTelefono(String telefono);
}
