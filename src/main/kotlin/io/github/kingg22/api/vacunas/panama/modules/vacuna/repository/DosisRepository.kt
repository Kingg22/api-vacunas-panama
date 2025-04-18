@file:Suppress("ktlint:standard:function-naming", "kotlin:S100")

package io.github.kingg22.api.vacunas.panama.modules.vacuna.repository

import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente
import io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.Dosis
import io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.Vacuna
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DosisRepository : JpaRepository<Dosis, UUID> {
    fun findTopByPacienteAndVacunaOrderByCreatedAtDesc(paciente: Paciente, vacuna: Vacuna): Dosis?

    fun findAllByPaciente_IdAndVacuna_IdOrderByCreatedAtDesc(paciente: UUID, id: UUID): List<Dosis>
}
