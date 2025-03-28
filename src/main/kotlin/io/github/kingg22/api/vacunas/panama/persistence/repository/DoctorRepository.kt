@file:Suppress("ktlint:standard:function-naming", "kotlin:S100")

package io.github.kingg22.api.vacunas.panama.persistence.repository

import io.github.kingg22.api.vacunas.panama.persistence.entity.Doctor
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface DoctorRepository : JpaRepository<Doctor, UUID> {
    fun findByUsuario_Id(idUser: UUID?): Optional<Doctor>
}
