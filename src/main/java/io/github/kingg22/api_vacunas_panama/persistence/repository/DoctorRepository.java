package io.github.kingg22.api_vacunas_panama.persistence.repository;

import io.github.kingg22.api_vacunas_panama.persistence.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    Optional<Doctor> findByIdoneidad(String idoneidad);

    Optional<Doctor> findByUsuario_Id(UUID idUser);

}
