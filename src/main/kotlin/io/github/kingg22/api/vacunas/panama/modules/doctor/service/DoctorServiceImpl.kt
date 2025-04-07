package io.github.kingg22.api.vacunas.panama.modules.doctor.service

import io.github.kingg22.api.vacunas.panama.modules.doctor.repository.DoctorRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DoctorServiceImpl(private val doctorRepository: DoctorRepository) : IDoctorService {
    override fun getDoctorByUserID(idUser: UUID) = doctorRepository.findByUsuario_Id(idUser)

    override fun getDoctorById(idDoctor: UUID) = doctorRepository.findById(idDoctor)
}
