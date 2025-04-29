package io.github.kingg22.api.vacunas.panama.modules.fabricante.persistence

import io.github.kingg22.api.vacunas.panama.modules.fabricante.entity.Fabricante
import java.util.UUID

/**
 * Persistence service interface for managing fabricantes.
 *
 * This service provides operations related to the persistence of fabricantes,
 * acting as an intermediate layer between the repositories and the service layer.
 * It encapsulates all JPA-related operations.
 */
interface FabricantePersistenceService {

    /**
     * Finds a fabricante by its licencia.
     *
     * @param licencia The licencia of the fabricante.
     * @return The fabricante entity if found, null otherwise.
     */
    suspend fun findByLicencia(licencia: String): Fabricante?

    /**
     * Finds a fabricante by its user ID.
     *
     * @param idUsuario The UUID of the user.
     * @return The fabricante entity if found, null otherwise.
     */
    suspend fun findByUsuarioId(idUsuario: UUID): Fabricante?
}
