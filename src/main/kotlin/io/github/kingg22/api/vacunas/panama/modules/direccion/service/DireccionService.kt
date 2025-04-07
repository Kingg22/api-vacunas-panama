package io.github.kingg22.api.vacunas.panama.modules.direccion.service

import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DireccionDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DistritoDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.ProvinciaDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Distrito
import jakarta.validation.Valid
import java.util.Optional

/**
 * Service interface for managing address-related operations such as creating, retrieving and searching for provinces,
 * districts, and specific addresses.
 */
interface DireccionService {

    /**
     * Retrieves a list of all districts mapped to DTOs.
     *
     * @return List of [DistritoDto] representing available districts.
     */
    fun getDistritosDto(): List<DistritoDto>

    /**
     * Retrieves a list of all provinces mapped to DTOs.
     *
     * @return List of [ProvinciaDto] representing available provinces.
     */
    fun getProvinciasDto(): List<ProvinciaDto>

    /**
     * Creates and persists a new [Direccion] entity from the given [DireccionDto].
     *
     * @param direccionDto DTO containing address data to persist.
     * @return The newly created [Direccion] entity.
     */
    fun createDireccion(direccionDto: @Valid DireccionDto): Direccion

    /**
     * Returns the default [Direccion] used by the system.
     * Useful for fallback or initial reference values.
     *
     * @return Default [Direccion] instance.
     */
    fun getDireccionDefault(): Direccion

    /**
     * Returns the default [Distrito] used by the system.
     * Useful for fallback or initial reference values.
     *
     * @return Default [Distrito] instance.
     */
    fun getDistritoDefault(): Distrito

    /**
     * Searches for an address entity matching the fields of the provided [DireccionDto].
     *
     * @param direccionDto DTO containing the address fields to search for.
     * @return [Optional] of [Direccion], present if a match is found, empty otherwise.
     */
    fun getDireccionByDto(direccionDto: DireccionDto): Optional<Direccion>
}
