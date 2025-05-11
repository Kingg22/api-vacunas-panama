package io.github.kingg22.api.vacunas.panama.modules.paciente.persistence

import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.PacienteDto
import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.toPaciente
import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente
import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.toPacienteDto
import io.github.kingg22.api.vacunas.panama.modules.paciente.repository.PacienteRepository
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona
import io.github.kingg22.api.vacunas.panama.util.logger
import io.github.kingg22.api.vacunas.panama.util.withTransaction
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

/**
 * Implementation of the PacientePersistenceService interface.
 *
 * This service provides operations related to the persistence of pacientes,
 * acting as an intermediate layer between the repositories and the service layer.
 * It encapsulates all JPA-related operations.
 */
@ApplicationScoped
class PacientePersistenceServiceImpl(private val pacienteRepository: PacienteRepository) : PacientePersistenceService {
    private val log = logger()

    /**
     * Finds a paciente by its ID.
     *
     * @param id The UUID of the paciente.
     * @return The paciente entity if found, null otherwise.
     */
    override suspend fun findPacienteById(id: UUID) = pacienteRepository.findByIdOrNull(id)

    /**
     * Finds all vaccine-disease records for a patient.
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
     * Saves a paciente entity using DTO.
     *
     * @param pacienteDto The paciente DTO to save.
     * @return The saved paciente DTO.
     */
    override suspend fun savePaciente(pacienteDto: PacienteDto): PacienteDto {
        val savedPaciente: Paciente? = withTransaction { _ ->
            log.trace("Saving pacienteDTO {}", pacienteDto)
            val paciente = pacienteDto.toPaciente()
            log.trace("Converted paciente {}", paciente)
            Persona.persist(paciente.persona).awaitSuspending()
            pacienteRepository.persistAndFlush(paciente).awaitSuspending()
        }
        checkNotNull(savedPaciente) { "Paciente not saved" }
        return savedPaciente.toPacienteDto()
    }
}
