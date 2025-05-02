package io.github.kingg22.api.vacunas.panama.modules.doctor.persistence

import io.github.kingg22.api.vacunas.panama.modules.doctor.repository.DoctorRepository
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

/**
 * Implementation of the DoctorPersistenceService interface.
 *
 * This service provides operations related to the persistence of doctors,
 * acting as an intermediate layer between the repositories and the service layer.
 * It encapsulates all JPA-related operations.
 */
@ApplicationScoped
class DoctorPersistenceServiceImpl(private val doctorRepository: DoctorRepository) : DoctorPersistenceService {

    /**
     * Finds a doctor by its ID.
     *
     * @param id The UUID of the doctor.
     * @return The doctor entity if found, null otherwise.
     */
    override suspend fun findDoctorById(id: UUID) = doctorRepository.findByIdOrNull(id)
}
