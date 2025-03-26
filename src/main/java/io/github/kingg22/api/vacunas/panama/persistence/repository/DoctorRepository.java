package io.github.kingg22.api.vacunas.panama.persistence.repository;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Doctor;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    Optional<Doctor> findByIdoneidad(String idoneidad);

    Optional<Doctor> findByUsuario_Id(UUID idUser);
}
