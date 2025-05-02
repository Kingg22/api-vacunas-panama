package io.github.kingg22.api.vacunas.panama.modules.persona.persistence

import io.github.kingg22.api.vacunas.panama.modules.persona.repository.PersonaRepository
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

/**
 * Implementation of the PersonaPersistenceService interface.
 *
 * This service provides operations related to the persistence of personas,
 * acting as an intermediate layer between the repositories and the service layer.
 * It encapsulates all JPA-related operations.
 */
@ApplicationScoped
class PersonaPersistenceServiceImpl(private val personaRepository: PersonaRepository) : PersonaPersistenceService {

    /**
     * Finds a persona by its cedula, pasaporte, or correo.
     *
     * @param cedula The cedula of the persona.
     * @param pasaporte The pasaporte of the persona.
     * @param correo The correo of the persona.
     * @return The persona entity if found, null otherwise.
     */
    override suspend fun findByCedulaOrPasaporteOrCorreo(cedula: String?, pasaporte: String?, correo: String?) =
        personaRepository.findByCedulaOrPasaporteOrCorreo(cedula, pasaporte, correo)

    /**
     * Finds a persona by its user ID.
     *
     * @param id The UUID of the user.
     * @return The persona entity if found, null otherwise.
     */
    override suspend fun findByUsuarioId(id: UUID) = personaRepository.findByUsuario_Id(id)

    /**
     * Finds a persona by its username.
     *
     * @param username The username of the user.
     * @return The persona entity if found, null otherwise.
     */
    override suspend fun findByUsuarioUsername(username: String) = personaRepository.findByUsuario_Username(username)
}
