package io.github.kingg22.api.vacunas.panama.modules.paciente.persistence

import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente
import io.github.kingg22.api.vacunas.panama.modules.paciente.repository.PacienteRepository
import jakarta.persistence.EntityManager
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import java.util.UUID

/**
 * Implementation of the PacientePersistenceService interface.
 *
 * This service provides operations related to the persistence of pacientes,
 * acting as an intermediate layer between the repositories and the service layer.
 * It encapsulates all JPA-related operations.
 */
@Service
class PacientePersistenceServiceImpl(
    private val entityManager: EntityManager,
    private val transactionTemplate: TransactionTemplate,
    private val pacienteRepository: PacienteRepository,
) : PacientePersistenceService {

    /**
     * Finds a paciente by its ID.
     *
     * @param id The UUID of the paciente.
     * @return The paciente entity if found, null otherwise.
     */
    override suspend fun findPacienteById(id: UUID) = pacienteRepository.findByIdOrNull(id)

    /**
     * Finds all vaccine-disease records for a paciente.
     *
     * @param id The UUID of the paciente.
     * @param vacuna The UUID of the vaccine (optional).
     * @return A list of DTOs containing vaccine-disease records.
     */
    override suspend fun findAllFromViewVacunaEnfermedad(id: UUID, vacuna: UUID?) =
        pacienteRepository.findAllFromViewVacunaEnfermedad(id, vacuna)

    /**
     * Finds a paciente by its temporary identification.
     *
     * @param idTemporal The temporary identification of the paciente.
     * @return The paciente entity if found, null otherwise.
     */
    override suspend fun findByIdentificacionTemporal(idTemporal: String?) =
        pacienteRepository.findByIdentificacionTemporal(idTemporal)

    /**
     * Finds a paciente by its persona's cedula.
     *
     * @param cedula The cedula of the persona.
     * @return The paciente entity if found, null otherwise.
     */
    override suspend fun findByPersonaCedula(cedula: String?) = pacienteRepository.findByPersonaCedula(cedula)

    /**
     * Finds a paciente by its persona's pasaporte.
     *
     * @param pasaporte The pasaporte of the persona.
     * @return The paciente entity if found, null otherwise.
     */
    override suspend fun findByPersonaPasaporte(pasaporte: String?) =
        pacienteRepository.findByPersonaPasaporte(pasaporte)

    /**
     * Finds a paciente by its persona's correo.
     *
     * @param correo The correo of the persona.
     * @return The paciente entity if found, null otherwise.
     */
    override suspend fun findByPersonaCorreo(correo: String?) = pacienteRepository.findByPersonaCorreo(correo)

    /**
     * Finds a paciente by its persona's telefono.
     *
     * @param telefono The telefono of the persona.
     * @return The paciente entity if found, null otherwise.
     */
    override suspend fun findByPersonaTelefono(telefono: String?) = pacienteRepository.findByPersonaTelefono(telefono)

    /**
     * Saves a paciente entity.
     *
     * @param paciente The paciente entity to save.
     * @return The saved paciente entity.
     */
    override suspend fun savePaciente(paciente: Paciente): Paciente {
        var savedPaciente: Paciente? = null
        transactionTemplate.execute {
            entityManager.persist(paciente)
            savedPaciente = paciente
        }
        checkNotNull(savedPaciente) { "Paciente not saved" }
        return savedPaciente
    }
}
