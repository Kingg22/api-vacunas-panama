package io.github.kingg22.api.vacunas.panama.modules.doctor.service

import io.github.kingg22.api.vacunas.panama.modules.doctor.entity.toDoctorDto
import io.github.kingg22.api.vacunas.panama.modules.doctor.persistence.DoctorPersistenceService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DoctorServiceImpl(private val doctorPersistenceService: DoctorPersistenceService) : DoctorService {
    override suspend fun getDoctorById(idDoctor: UUID) =
        doctorPersistenceService.findDoctorById(idDoctor)?.toDoctorDto()
}
