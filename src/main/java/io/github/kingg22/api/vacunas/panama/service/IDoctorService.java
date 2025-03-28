package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Doctor;
import java.util.Optional;
import java.util.UUID;

public interface IDoctorService {
    Optional<Doctor> getDoctorByUserID(UUID idUser);

    Optional<Doctor> getDoctorById(UUID idDoctor);
}
