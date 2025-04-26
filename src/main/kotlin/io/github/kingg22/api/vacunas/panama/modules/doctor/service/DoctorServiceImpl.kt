package io.github.kingg22.api.vacunas.panama.modules.doctor.service

import io.github.kingg22.api.vacunas.panama.modules.doctor.entity.toDoctorDto
import io.github.kingg22.api.vacunas.panama.modules.doctor.repository.DoctorRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DoctorServiceImpl(private val doctorRepository: DoctorRepository) : DoctorService {
    override suspend fun getDoctorById(idDoctor: UUID) = doctorRepository.findByIdOrNull(idDoctor)?.toDoctorDto()
}
