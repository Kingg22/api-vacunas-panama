package io.github.kingg22.api.vacunas.panama.service

import io.github.kingg22.api.vacunas.panama.persistence.entity.Doctor
import java.util.Optional
import java.util.UUID

interface IDoctorService {
    fun getDoctorByUserID(idUser: UUID): Optional<Doctor>

    fun getDoctorById(idDoctor: UUID): Optional<Doctor>
}
