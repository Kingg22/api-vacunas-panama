package io.github.kingg22.api.vacunas.panama.modules.paciente.persistence

import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.PacienteDto
import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.ViewPacienteVacunaEnfermedadDto
import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente
import java.util.UUID

/**
 * Persistence service interface for managing pacientes.
 *
 * This service provides operations related to the persistence of pacientes,
 * acting as an intermediate layer between the repositories and the service layer.
 * It encapsulates all JPA-related operations.
 */
interface PacientePersistenceService {

    /**
     * Finds a paciente by its ID.
     *
     * @param id The UUID of the paciente.
     * @return The paciente entity if found, null otherwise.
     */
    suspend fun findPacienteById(id: UUID): Paciente?

    /**
     * Finds all vaccine-disease records for a paciente.
     *
     * @param id The UUID of the paciente.
     * @param vacuna The UUID of the vaccine (optional).
     * @return A list of DTOs containing vaccine-disease records.
     */
    suspend fun findAllFromViewVacunaEnfermedad(id: UUID, vacuna: UUID? = null): List<ViewPacienteVacunaEnfermedadDto>

    /**
     * Finds a paciente by its temporary identification.
     *
     * @param idTemporal The temporary identification of the paciente.
     * @return The paciente entity if found, null otherwise.
     */
    suspend fun findByIdentificacionTemporal(idTemporal: String?): Paciente?

    /**
     * Finds a paciente by its persona's cedula.
     *
     * @param cedula The cedula of the persona.
     * @return The paciente entity if found, null otherwise.
     */
    suspend fun findByPersonaCedula(cedula: String?): Paciente?

    /**
     * Finds a paciente by its persona's pasaporte.
     *
     * @param pasaporte The pasaporte of the persona.
     * @return The paciente entity if found, null otherwise.
     */
    suspend fun findByPersonaPasaporte(pasaporte: String?): Paciente?

    /**
     * Finds a paciente by its persona's correo.
     *
     * @param correo The correo of the persona.
     * @return The paciente entity if found, null otherwise.
     */
    suspend fun findByPersonaCorreo(correo: String?): Paciente?

    /**
     * Finds a paciente by its persona's telefono.
     *
     * @param telefono The telefono of the persona.
     * @return The paciente entity if found, null otherwise.
     */
    suspend fun findByPersonaTelefono(telefono: String?): Paciente?

    /**
     * Saves a paciente entity using DTO.
     *
     * @param pacienteDto The paciente DTO to save.
     * @return The saved paciente DTO.
     */
    suspend fun savePaciente(pacienteDto: PacienteDto): PacienteDto
}
