package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.modules.doctor.entity.Doctor;
import io.github.kingg22.api.vacunas.panama.modules.doctor.repository.DoctorRepository;
import io.github.kingg22.api.vacunas.panama.modules.doctor.service.IDoctorService;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorService implements IDoctorService {
    private final DoctorRepository doctorRepository;

    @NotNull
    public Optional<Doctor> getDoctorByUserID(@NotNull UUID idUser) {
        return this.doctorRepository.findByUsuario_Id(idUser);
    }

    @NotNull
    public Optional<Doctor> getDoctorById(@NotNull UUID idDoctor) {
        return this.doctorRepository.findById(idDoctor);
    }
}
