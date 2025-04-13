package io.github.kingg22.api.vacunas.panama.modules.doctor.service

import io.github.kingg22.api.vacunas.panama.modules.doctor.dto.DoctorDto
import java.util.Optional
import java.util.UUID

/** Service interface for managing and retrieving doctor-related information. */
fun interface DoctorService {

    /**
     * Retrieves a [DoctorDto] by its unique doctor ID.
     *
     * @param idDoctor UUID of the doctor to retrieve.
     * @return [Optional] containing the doctor if found, or empty if not.
     */
    fun getDoctorById(idDoctor: UUID): Optional<DoctorDto>
}
