package io.github.kingg22.api.vacunas.panama.modules.doctor.persistence

import io.github.kingg22.api.vacunas.panama.modules.doctor.entity.Doctor
import java.util.UUID

/**
 * Persistence service interface for managing doctors.
 *
 * This service provides operations related to the persistence of doctors,
 * acting as an intermediate layer between the repositories and the service layer.
 * It encapsulates all JPA-related operations.
 */
fun interface DoctorPersistenceService {

    /**
     * Finds a doctor by its ID.
     *
     * @param id The UUID of the doctor.
     * @return The doctor entity if found, null otherwise.
     */
    suspend fun findDoctorById(id: UUID): Doctor?
}
