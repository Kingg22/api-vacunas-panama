package io.github.kingg22.api.vacunas.panama.modules.vacuna.service

import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.DosisDto
import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.InsertDosisDto
import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.VacunaFabricanteDto
import io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.Vacuna
import io.github.kingg22.api.vacunas.panama.response.ApiContentResponse
import java.util.Optional
import java.util.UUID

/**
 * Service interface for managing vaccines and doses.
 *
 * This service provides operations related to vaccines, including creating doses, retrieving
 * vaccine information by manufacturer, and querying doses and vaccines for specific patients.
 * It acts as the main service for vaccine-related data manipulation and retrieval in the system.
 */
interface VacunaService {

    /**
     * Creates a new dose entry based on the provided DTO.
     *
     * @param insertDosisDto DTO containing information about the dose to be created.
     * @return API response indicating the result of the operation.
     */
    fun createDosis(insertDosisDto: InsertDosisDto): ApiContentResponse

    /**
     * Retrieves a list of vaccines by their manufacturer.
     *
     * @return A list of DTOs containing vaccine manufacturer details.
     */
    fun getVacunasFabricante(): List<VacunaFabricanteDto>

    /**
     * Retrieves doses administered to a patient for a specific vaccine.
     *
     * @param idPaciente The UUID of the patient.
     * @param idVacuna The UUID of the vaccine.
     * @return A list of DTOs containing the doses administered to the patient for the given vaccine.
     */
    fun getDosisByIdPacienteIdVacuna(idPaciente: UUID, idVacuna: UUID): List<DosisDto>

    /**
     * Retrieves a specific vaccine by its ID.
     *
     * @param id The UUID of the vaccine.
     * @return An Optional containing the vaccine if found, or an empty Optional if not found.
     */
    fun getVacunaById(id: UUID): Optional<Vacuna>
}
