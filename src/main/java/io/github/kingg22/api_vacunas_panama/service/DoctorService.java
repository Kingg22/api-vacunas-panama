package io.github.kingg22.api_vacunas_panama.service;

import io.github.kingg22.api_vacunas_panama.persistence.entity.Doctor;
import io.github.kingg22.api_vacunas_panama.persistence.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;

    Optional<Doctor> getDoctorByUserID(UUID idUser) {
        return this.doctorRepository.findByUsuario_Id(idUser);
    }

    Optional<Doctor> getDoctorById(UUID idDoctor) {
        return this.doctorRepository.findById(idDoctor);
    }

}
