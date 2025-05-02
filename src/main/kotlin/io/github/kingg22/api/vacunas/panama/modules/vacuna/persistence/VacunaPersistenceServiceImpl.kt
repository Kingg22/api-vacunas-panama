package io.github.kingg22.api.vacunas.panama.modules.vacuna.persistence

import io.github.kingg22.api.vacunas.panama.modules.doctor.entity.Doctor
import io.github.kingg22.api.vacunas.panama.modules.paciente.domain.PacienteModel
import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente
import io.github.kingg22.api.vacunas.panama.modules.sede.entity.Sede
import io.github.kingg22.api.vacunas.panama.modules.vacuna.domain.DosisModel
import io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.Dosis
import io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.Vacuna
import io.github.kingg22.api.vacunas.panama.modules.vacuna.repository.DosisRepository
import io.github.kingg22.api.vacunas.panama.modules.vacuna.repository.VacunaRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManager
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.util.UUID

/**
 * Implementation of the VacunaPersistenceService interface.
 *
 * This service provides operations related to the persistence of vaccines and doses,
 * acting as an intermediate layer between the repositories and the service layer.
 * It encapsulates all JPA-related operations.
 */
@ApplicationScoped
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

    override suspend fun findTopDosisByPacienteAndVacuna(paciente: PacienteModel, vacuna: Vacuna): Dosis? {
        checkNotNull(paciente.persona.id) { "Paciente ID must not be null" }
        val pacienteEntity = entityManager.find(Paciente::class.java, paciente.persona.id)
            ?: throw IllegalArgumentException("Paciente with ID ${paciente.persona.id} does not exist")
        return dosisRepository.findTopByPacienteAndVacunaOrderByCreatedAtDesc(pacienteEntity, vacuna)
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

    override suspend fun createAndSaveDosis(dosisModel: DosisModel): Dosis {
        var savedDosis: Dosis? = null
        val pacienteId = dosisModel.paciente.persona.id
        val vacunaId = dosisModel.vacuna.id
        val sedeId = dosisModel.sede.id
        val numeroDosis = dosisModel.numeroDosis
        val lote = dosisModel.lote
        val doctorId = dosisModel.doctor?.id
        val fechaAplicacion = dosisModel.fechaAplicacion
        checkNotNull(pacienteId) { "Paciente ID must not be null" }
        checkNotNull(vacunaId) { "Vacuna ID must not be null" }
        checkNotNull(sedeId) { "Sede ID must not be null" }

        transactionTemplate.execute {
            // Find entities using entityManager.find instead of fromModel mappers
            val paciente = entityManager.find(Paciente::class.java, pacienteId)
                ?: throw IllegalArgumentException("Paciente with ID $pacienteId does not exist")

            val vacuna = entityManager.find(Vacuna::class.java, vacunaId)
                ?: throw IllegalArgumentException("Vacuna with ID $vacunaId does not exist")

            val sede = entityManager.find(Sede::class.java, sedeId)
                ?: throw IllegalArgumentException("Sede with ID $sedeId does not exist")

            val doctor = doctorId?.let {
                entityManager.find(Doctor::class.java, it)
            }

            val dosis = Dosis(
                paciente = paciente,
                numeroDosis = numeroDosis,
                vacuna = vacuna,
                sede = sede,
                lote = lote,
                doctor = doctor,
                fechaAplicacion = fechaAplicacion,
                createdAt = LocalDateTime.now(UTC),
            )

            entityManager.persist(dosis)
            savedDosis = dosis
        }
        checkNotNull(savedDosis) {
            "Dosis not saved for paciente=$pacienteId, sede=$sedeId, numero dosis=$numeroDosis, fecha aplicación=$fechaAplicacion, lote=$lote, doctor=$doctorId"
        }
        return savedDosis
    }
}
