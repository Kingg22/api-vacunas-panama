package io.github.kingg22.api.vacunas.panama.modules.sede.persistence

import io.github.kingg22.api.vacunas.panama.modules.common.dto.UUIDNombreDto
import io.github.kingg22.api.vacunas.panama.modules.sede.entity.Sede
import java.util.UUID

/**
 * Persistence service interface for managing sedes.
 *
 * This service provides operations related to the persistence of sedes,
 * acting as an intermediate layer between the repositories and the service layer.
 * It encapsulates all JPA-related operations.
 */
interface SedePersistenceService {

    /**
     * Finds a sede by its ID.
     *
     * @param id The UUID of the sede.
     * @return The sede entity if found, null otherwise.
     */
    suspend fun findSedeById(id: UUID): Sede?

    /**
     * Finds all sedes with their IDs and names.
     *
     * @return A list of DTOs containing sede ID and name.
     */
    suspend fun findAllIdAndNombre(): List<UUIDNombreDto>
}
