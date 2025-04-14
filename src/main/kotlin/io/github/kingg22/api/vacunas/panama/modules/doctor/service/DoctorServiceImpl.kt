package io.github.kingg22.api.vacunas.panama.modules.doctor.service

import io.github.kingg22.api.vacunas.panama.modules.doctor.dto.DoctorDto
import io.github.kingg22.api.vacunas.panama.modules.doctor.entity.toDoctorDto
import io.github.kingg22.api.vacunas.panama.modules.doctor.repository.DoctorRepository
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.UUID

@Service
class DoctorServiceImpl(private val doctorRepository: DoctorRepository) : DoctorService {
    override fun getDoctorById(idDoctor: UUID): Optional<DoctorDto> =
        doctorRepository.findById(idDoctor).map { it.toDoctorDto() }
}
