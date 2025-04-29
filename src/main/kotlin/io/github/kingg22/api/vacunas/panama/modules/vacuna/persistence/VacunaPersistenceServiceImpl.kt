package io.github.kingg22.api.vacunas.panama.modules.vacuna.persistence

import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente
import io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.Dosis
import io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.Vacuna
import io.github.kingg22.api.vacunas.panama.modules.vacuna.repository.DosisRepository
import io.github.kingg22.api.vacunas.panama.modules.vacuna.repository.VacunaRepository
import jakarta.persistence.EntityManager
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import java.util.UUID

/**
 * Implementation of the VacunaPersistenceService interface.
 *
 * This service provides operations related to the persistence of vaccines and doses,
 * acting as an intermediate layer between the repositories and the service layer.
 * It encapsulates all JPA-related operations.
 */
@Service
class VacunaPersistenceServiceImpl(
    private val entityManager: EntityManager,
    private val transactionTemplate: TransactionTemplate,
    private val vacunaRepository: VacunaRepository,
    private val dosisRepository: DosisRepository,
) : VacunaPersistenceService {

    /**
     * Finds a vaccine by its ID.
     *
     * @param id The UUID of the vaccine.
     * @return The vaccine entity if found, null otherwise.
     */
    override suspend fun findVacunaById(id: UUID) = vacunaRepository.findByIdOrNull(id)

    /**
     * Finds the most recent dose for a patient and vaccine.
     *
     * @param paciente The patient entity.
     * @param vacuna The vaccine entity.
     * @return The most recent dose entity if found, null otherwise.
     */
    override suspend fun findTopDosisByPacienteAndVacuna(paciente: Paciente, vacuna: Vacuna) =
        dosisRepository.findTopByPacienteAndVacunaOrderByCreatedAtDesc(paciente, vacuna)

    /**
     * Saves a new dose entity.
     *
     * @param dosis The dose entity to save.
     * @return The saved dose entity.
     */
    override suspend fun saveDosis(dosis: Dosis): Dosis {
        var savedDosis: Dosis? = null
        transactionTemplate.execute {
            entityManager.persist(dosis)
            savedDosis = dosis
        }
        checkNotNull(savedDosis) { "Dosis not saved" }
        return savedDosis
    }

    /**
     * Finds all vaccines with their manufacturers.
     *
     * @return A list of DTOs containing vaccine manufacturer details.
     */
    override suspend fun findAllVacunasWithFabricantes() = vacunaRepository.findAllIdAndNombreAndFabricante()

    /**
     * Finds all doses for a patient and vaccine.
     *
     * @param idPaciente The UUID of the patient.
     * @param idVacuna The UUID of the vaccine.
     * @return A list of dose entities.
     */
    override suspend fun findAllDosisByPacienteIdAndVacunaId(idPaciente: UUID, idVacuna: UUID) =
        dosisRepository.findAllByPaciente_IdAndVacuna_IdOrderByCreatedAtDesc(idPaciente, idVacuna)
}
