package io.github.kingg22.api_vacunas_panama.persistence.repository;

import io.github.kingg22.api_vacunas_panama.persistence.entity.Dosis;
import io.github.kingg22.api_vacunas_panama.persistence.entity.Paciente;
import io.github.kingg22.api_vacunas_panama.persistence.entity.Vacuna;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DosisRepository extends JpaRepository<Dosis, UUID> {

    Optional<Dosis> findTopByPacienteAndVacunaOrderByCreatedAtDesc(Paciente paciente, Vacuna vacuna);

    List<Dosis> findAllByPaciente_IdAndVacuna_IdOrderByCreatedAtDesc(UUID paciente, UUID id);

}
