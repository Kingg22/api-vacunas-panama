package io.github.kingg22.api.vacunas.panama.modules.sede.persistence

import io.github.kingg22.api.vacunas.panama.modules.sede.repository.SedeRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

/**
 * Implementation of the SedePersistenceService interface.
 *
 * This service provides operations related to the persistence of sedes,
 * acting as an intermediate layer between the repositories and the service layer.
 * It encapsulates all JPA-related operations.
 */
@Service
class SedePersistenceServiceImpl(private val sedeRepository: SedeRepository) : SedePersistenceService {

    /**
     * Finds a sede by its ID.
     *
     * @param id The UUID of the sede.
     * @return The sede entity if found, null otherwise.
     */
    override suspend fun findSedeById(id: UUID) = sedeRepository.findByIdOrNull(id)

    /**
     * Finds all sedes with their IDs and names.
     *
     * @return A list of DTOs containing sede ID and name.
     */
    override suspend fun findAllIdAndNombre() = sedeRepository.findAllIdAndNombre()
}
