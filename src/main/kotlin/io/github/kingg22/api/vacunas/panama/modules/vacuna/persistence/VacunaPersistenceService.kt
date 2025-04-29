package io.github.kingg22.api.vacunas.panama.modules.vacuna.persistence

import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente
import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.VacunaFabricanteDto
import io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.Dosis
import io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.Vacuna
import java.util.UUID

/**
 * Persistence service interface for managing vaccines and doses.
 *
 * This service provides operations related to the persistence of vaccines and doses,
 * acting as an intermediate layer between the repositories and the service layer.
 * It encapsulates all JPA-related operations.
 */
interface VacunaPersistenceService {

    /**
     * Finds a vaccine by its ID.
     *
     * @param id The UUID of the vaccine.
     * @return The vaccine entity if found, null otherwise.
     */
    suspend fun findVacunaById(id: UUID): Vacuna?

    /**
     * Finds the most recent dose for a patient and vaccine.
     *
     * @param paciente The patient entity.
     * @param vacuna The vaccine entity.
     * @return The most recent dose entity if found, null otherwise.
     */
    suspend fun findTopDosisByPacienteAndVacuna(paciente: Paciente, vacuna: Vacuna): Dosis?

    /**
     * Saves a new dose entity.
     *
     * @param dosis The dose entity to save.
     * @return The saved dose entity.
     */
    suspend fun saveDosis(dosis: Dosis): Dosis

    /**
     * Finds all vaccines with their manufacturers.
     *
     * @return A list of DTOs containing vaccine manufacturer details.
     */
    suspend fun findAllVacunasWithFabricantes(): List<VacunaFabricanteDto>

    /**
     * Finds all doses for a patient and vaccine.
     *
     * @param idPaciente The UUID of the patient.
     * @param idVacuna The UUID of the vaccine.
     * @return A list of dose entities.
     */
    suspend fun findAllDosisByPacienteIdAndVacunaId(idPaciente: UUID, idVacuna: UUID): List<Dosis>
}
