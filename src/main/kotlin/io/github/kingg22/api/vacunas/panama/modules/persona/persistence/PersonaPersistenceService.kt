package io.github.kingg22.api.vacunas.panama.modules.persona.persistence

import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona
import java.util.UUID

/**
 * Persistence service interface for managing personas.
 *
 * This service provides operations related to the persistence of personas,
 * acting as an intermediate layer between the repositories and the service layer.
 * It encapsulates all JPA-related operations.
 */
interface PersonaPersistenceService {

    /**
     * Finds a persona by its cedula, pasaporte, or correo.
     *
     * @param cedula The cedula of the persona.
     * @param pasaporte The pasaporte of the persona.
     * @param correo The correo of the persona.
     * @return The persona entity if found, null otherwise.
     */
    suspend fun findByCedulaOrPasaporteOrCorreo(cedula: String?, pasaporte: String?, correo: String?): Persona?

    /**
     * Finds a persona by its user ID.
     *
     * @param id The UUID of the user.
     * @return The persona entity if found, null otherwise.
     */
    suspend fun findByUsuarioId(id: UUID): Persona?

    /**
     * Finds a persona by its username.
     *
     * @param username The username of the user.
     * @return The persona entity if found, null otherwise.
     */
    suspend fun findByUsuarioUsername(username: String): Persona?
}
