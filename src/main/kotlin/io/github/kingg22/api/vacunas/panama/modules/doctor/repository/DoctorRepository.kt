package io.github.kingg22.api.vacunas.panama.modules.doctor.repository

import io.github.kingg22.api.vacunas.panama.modules.doctor.entity.Doctor
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DoctorRepository : JpaRepository<Doctor, UUID>
