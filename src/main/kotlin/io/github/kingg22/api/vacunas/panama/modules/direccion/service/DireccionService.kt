package io.github.kingg22.api.vacunas.panama.modules.direccion.service

import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DireccionDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DistritoDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.ProvinciaDto

/**
 * Service interface for managing address-related operations such as creating, retrieving, and searching for provinces,
 * districts, and specific addresses.
 */
interface DireccionService {

    /**
     * Retrieves a list of all districts mapped to DTOs.
     *
     * @return List of [DistritoDto] representing available districts.
     */
    suspend fun getDistritosDto(): List<DistritoDto>

    /**
     * Retrieves a list of all provinces mapped to DTOs.
     *
     * @return List of [ProvinciaDto] representing available provinces.
     */
    suspend fun getProvinciasDto(): List<ProvinciaDto>

    /**
     * Creates and persists a new `Direccion` entity from the given [DireccionDto].
     *
     * @param direccionDto DTO containing address data to persist.
     * @return The newly created [DireccionDto] representing the saved model.
     */
    suspend fun createDireccion(direccionDto: DireccionDto): DireccionDto

    /**
     * Searches for an address entity matching the fields of the provided [DireccionDto].
     *
     * @param direccionDto DTO containing the address fields to search for.
     * @return [DireccionDto], present if a match is found, null otherwise.
     */
    suspend fun getDireccionByDto(direccionDto: DireccionDto): DireccionDto?
}
