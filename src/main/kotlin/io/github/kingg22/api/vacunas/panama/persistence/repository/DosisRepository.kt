@file:Suppress("ktlint:standard:function-naming", "kotlin:S100")

package io.github.kingg22.api.vacunas.panama.persistence.repository

import io.github.kingg22.api.vacunas.panama.persistence.entity.Dosis
import io.github.kingg22.api.vacunas.panama.persistence.entity.Paciente
import io.github.kingg22.api.vacunas.panama.persistence.entity.Vacuna
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface DosisRepository : JpaRepository<Dosis, UUID> {
    fun findTopByPacienteAndVacunaOrderByCreatedAtDesc(paciente: Paciente, vacuna: Vacuna): Optional<Dosis>

    fun findAllByPaciente_IdAndVacuna_IdOrderByCreatedAtDesc(paciente: UUID, id: UUID): List<Dosis>
}
