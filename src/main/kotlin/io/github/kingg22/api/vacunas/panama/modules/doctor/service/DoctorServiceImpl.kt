package io.github.kingg22.api.vacunas.panama.modules.doctor.service

import io.github.kingg22.api.vacunas.panama.modules.doctor.dto.toDoctorModel
import io.github.kingg22.api.vacunas.panama.modules.doctor.entity.toDoctorDto
import io.github.kingg22.api.vacunas.panama.modules.doctor.persistence.DoctorPersistenceService
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class DoctorServiceImpl(private val doctorPersistenceService: DoctorPersistenceService) : DoctorService {
    override suspend fun getDoctorById(idDoctor: UUID) =
        doctorPersistenceService.findDoctorById(idDoctor)?.toDoctorDto()?.toDoctorModel()
}
