package io.github.kingg22.api.vacunas.panama.modules.paciente.service

import io.github.kingg22.api.vacunas.panama.modules.paciente.domain.PacienteModel
import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.PacienteDto
import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.ViewPacienteVacunaEnfermedadDto
import io.github.kingg22.api.vacunas.panama.response.ApiContentResponse
import java.util.UUID

/**
 * Service interface for managing patient-related operations, including validation,
 * creation, retrieval, and vaccine-disease view generation.
 */
interface PacienteService {
    /**
     * Creates a new `PacienteModel` and associated user if validation is successful.
     *
     * @param pacienteDto DTO containing the patient information to create.
     * @return [ApiContentResponse] with creation result and metadata.
     */
    suspend fun createPaciente(pacienteDto: PacienteDto): ApiContentResponse

    /**
     * Retrieves a patient DTO by its internal ID.
     *
     * @param id UUID of the patient.
     * @return [PacienteDto] containing patient information.
     */
    suspend fun getPacienteDtoById(id: UUID): PacienteDto?

    /**
     * Retrieves a [PacienteModel] by its unique ID.
     *
     * @param idPaciente UUID of the patient.
     * @return [PacienteModel] if found, or null if not.
     */
    suspend fun getPacienteById(idPaciente: UUID): PacienteModel?

    /**
     * Retrieves a list of vaccines and diseases linked to the given patient.
     * The view combines vaccine application and related disease data.
     *
     * @param id UUID of the patient.
     * @return List of [ViewPacienteVacunaEnfermedadDto] representing vaccine-disease records.
     */
    suspend fun getViewVacunaEnfermedad(id: UUID): List<ViewPacienteVacunaEnfermedadDto>
}
