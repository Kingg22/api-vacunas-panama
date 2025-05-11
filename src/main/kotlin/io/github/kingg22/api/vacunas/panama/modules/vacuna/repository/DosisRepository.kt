package io.github.kingg22.api.vacunas.panama.modules.vacuna.repository

import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente
import io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.Dosis
import io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.Vacuna
import io.github.kingg22.api.vacunas.panama.util.withSession
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepositoryBase
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class DosisRepository : PanacheRepositoryBase<Dosis, UUID> {
    suspend fun findTopByPacienteAndVacunaOrderByCreatedAtDesc(paciente: Paciente, vacuna: Vacuna): Dosis? =
        withSession {
            find(
                "paciente = :paciente AND vacuna = :vacuna ORDER BY createdAt DESC",
                mapOf("paciente" to paciente, "vacuna" to vacuna),
            ).firstResult().awaitSuspending()
        }

    suspend fun findAllByPacienteIdAndVacunaIdOrderByCreatedAtDesc(paciente: UUID, id: UUID): List<Dosis> =
        withSession {
            list(
                "paciente.id = :paciente AND vacuna.id = :id ORDER BY createdAt DESC",
                mapOf("paciente" to paciente, "id" to id),
            ).awaitSuspending()
        }
}
