package io.github.kingg22.api.vacunas.panama.modules.doctor.service

import io.github.kingg22.api.vacunas.panama.modules.doctor.domain.DoctorModel
import java.util.UUID

/** Service interface for managing and retrieving doctor-related information. */
fun interface DoctorService {

    /**
     * Retrieves a [DoctorModel] by its unique doctor ID.
     *
     * @param idDoctor UUID of the doctor to retrieve.
     * @return The doctor if found, or null if not.
     */
    suspend fun getDoctorById(idDoctor: UUID): DoctorModel?
}
