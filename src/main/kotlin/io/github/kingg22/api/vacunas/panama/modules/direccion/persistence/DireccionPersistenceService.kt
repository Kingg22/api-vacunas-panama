package io.github.kingg22.api.vacunas.panama.modules.direccion.persistence

import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DireccionDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DistritoDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.ProvinciaDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion
import java.util.UUID

/**
 * Persistence service interface for managing addresses.
 *
 * This service provides operations related to the persistence of addresses,
 * acting as an intermediate layer between the repositories and the service layer.
 * It encapsulates all JPA-related operations.
 */
interface DireccionPersistenceService {

    /**
     * Finds all districts.
     *
     * @return A list of district DTOs.
     */
    suspend fun findAllDistritos(): List<DistritoDto>

    /**
     * Finds all provinces.
     *
     * @return A list of province DTOs.
     */
    suspend fun findAllProvincias(): List<ProvinciaDto>

    /**
     * Finds a district by its ID.
     *
     * @param id The ID of the district.
     * @return The district DTO if found, null otherwise.
     */
    suspend fun findDistritoById(id: Short): DistritoDto?

    /**
     * Saves a new address entity using DTO.
     *
     * @param direccionDto The address DTO to save.
     * @return The saved address DTO.
     */
    suspend fun saveDireccion(direccionDto: DireccionDto): DireccionDto

    /**
     * Finds an address by its ID.
     *
     * @param id The ID of the address.
     * @return The address entity if found, null otherwise.
     */
    suspend fun findDireccionById(id: UUID): Direccion?

    /**
     * Finds addresses by description and district ID.
     *
     * @param descripcion The description of the address.
     * @param distritoId The ID of the district.
     * @return A list of address entities.
     */
    suspend fun findDireccionByDescripcionAndDistritoId(descripcion: String, distritoId: Short): List<Direccion>

    /**
     * Finds addresses by description and district name.
     *
     * @param descripcion The description of the address.
     * @param distritoNombre The name of the district.
     * @return A list of address entities.
     */
    suspend fun findDireccionByDescripcionAndDistritoNombre(
        descripcion: String,
        distritoNombre: String,
    ): List<Direccion>

    /**
     * Finds addresses by description starting with a given string.
     *
     * @param descripcion The description prefix.
     * @return A list of address entities.
     */
    suspend fun findDireccionByDescripcionStartingWith(descripcion: String): List<Direccion>
}
