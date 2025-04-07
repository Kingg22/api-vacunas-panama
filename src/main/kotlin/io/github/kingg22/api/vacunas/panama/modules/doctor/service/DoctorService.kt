package io.github.kingg22.api.vacunas.panama.modules.doctor.service

import io.github.kingg22.api.vacunas.panama.modules.doctor.entity.Doctor
import java.util.Optional
import java.util.UUID

/** Service interface for managing and retrieving doctor-related information. */
interface DoctorService {

    /**
     * Retrieves a [Doctor] entity associated with the specified user ID.
     *
     * @param idUser UUID of the user linked to the doctor.
     * @return [Optional] containing the doctor if found, or empty if not.
     */
    fun getDoctorByUserID(idUser: UUID): Optional<Doctor>

    /**
     * Retrieves a [Doctor] entity by its unique doctor ID.
     *
     * @param idDoctor UUID of the doctor to retrieve.
     * @return [Optional] containing the doctor if found, or empty if not.
     */
    fun getDoctorById(idDoctor: UUID): Optional<Doctor>
}
