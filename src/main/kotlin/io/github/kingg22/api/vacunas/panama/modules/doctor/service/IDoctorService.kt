package io.github.kingg22.api.vacunas.panama.modules.doctor.service

import io.github.kingg22.api.vacunas.panama.modules.doctor.entity.Doctor
import java.util.Optional
import java.util.UUID

interface IDoctorService {
    fun getDoctorByUserID(idUser: UUID): Optional<Doctor>

    fun getDoctorById(idDoctor: UUID): Optional<Doctor>
}
