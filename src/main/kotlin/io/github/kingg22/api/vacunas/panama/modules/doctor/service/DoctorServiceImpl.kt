package io.github.kingg22.api.vacunas.panama.modules.doctor.service

import io.github.kingg22.api.vacunas.panama.modules.doctor.entity.toDoctorDto
import io.github.kingg22.api.vacunas.panama.modules.doctor.repository.DoctorRepository
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Service
class DoctorServiceImpl(private val doctorRepository: DoctorRepository) : DoctorService {
    override fun getDoctorById(idDoctor: UUID) = doctorRepository.findById(idDoctor).getOrNull()?.toDoctorDto()
}
