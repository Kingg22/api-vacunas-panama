package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Doctor;
import io.github.kingg22.api.vacunas.panama.persistence.repository.DoctorRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorService implements IDoctorService {
    private final DoctorRepository doctorRepository;

    public Optional<Doctor> getDoctorByUserID(UUID idUser) {
        return this.doctorRepository.findByUsuario_Id(idUser);
    }

    public Optional<Doctor> getDoctorById(UUID idDoctor) {
        return this.doctorRepository.findById(idDoctor);
    }
}
