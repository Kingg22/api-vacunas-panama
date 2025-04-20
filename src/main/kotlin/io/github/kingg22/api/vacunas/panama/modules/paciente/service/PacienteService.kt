package io.github.kingg22.api.vacunas.panama.modules.paciente.service

import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.PacienteDto
import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.ViewPacienteVacunaEnfermedadDto
import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente
import io.github.kingg22.api.vacunas.panama.response.ApiContentResponse
import java.util.UUID

/**
 * Service interface for managing patient-related operations, including validation,
 * creation, retrieval, and vaccine-disease view generation.
 */
interface PacienteService {
    /**
     * Creates a new `Paciente` and associated user if validation is successful.
     *
     * @param pacienteDto DTO containing the patient information to create.
     * @return [ApiContentResponse] with creation result and metadata.
     */
    fun createPaciente(pacienteDto: PacienteDto): ApiContentResponse

    /**
     * Retrieves a patient DTO by its internal ID.
     *
     * @param id UUID of the patient.
     * @return [PacienteDto] containing patient information.
     */
    fun getPacienteDtoById(id: UUID): PacienteDto?

    /**
     * Retrieves a [PacienteDto] by its unique ID.
     *
     * @param idPaciente UUID of the patient.
     * @return [PacienteDto] if found, or null if not.
     */
    @Deprecated("Use DTO instead", replaceWith = ReplaceWith("getPacienteDtoById(idPaciente)"))
    fun getPacienteById(idPaciente: UUID): Paciente?

    /**
     * Retrieves a list of vaccines and diseases linked to the given patient.
     * The view combines vaccine application and related disease data.
     *
     * @param id UUID of the patient.
     * @return List of [ViewPacienteVacunaEnfermedadDto] representing vaccine-disease records.
     */
    fun getViewVacunaEnfermedad(id: UUID): List<ViewPacienteVacunaEnfermedadDto>
}
