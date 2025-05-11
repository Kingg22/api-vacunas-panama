package io.github.kingg22.api.vacunas.panama.modules.direccion.persistence

import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DireccionDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.toDireccion
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.toDireccionDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.toDistritoDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.toListDistritoDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.toListProvinciaDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.repository.DireccionRepository
import io.github.kingg22.api.vacunas.panama.modules.direccion.repository.DistritoRepository
import io.github.kingg22.api.vacunas.panama.modules.direccion.repository.ProvinciaRepository
import io.github.kingg22.api.vacunas.panama.util.logger
import io.github.kingg22.api.vacunas.panama.util.withTransaction
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

/**
 * Implementation of the DireccionPersistenceService interface.
 *
 * This service provides operations related to the persistence of addresses,
 * acting as an intermediate layer between the repositories and the service layer.
 * It encapsulates all JPA-related operations.
 */
@ApplicationScoped
class DireccionPersistenceServiceImpl(
    private val direccionRepository: DireccionRepository,
    private val distritoRepository: DistritoRepository,
    private val provinciaRepository: ProvinciaRepository,
) : DireccionPersistenceService {
    private val log = logger()

    /**
     * Finds all districts.
     *
     * @return A list of district DTOs.
     */
    override suspend fun findAllDistritos() = distritoRepository.listAllDistritos().toListDistritoDto()

    /**
     * Finds all provinces.
     *
     * @return A list of province DTOs.
     */
    override suspend fun findAllProvincias() = provinciaRepository.listAllProvincias().toListProvinciaDto()

    /**
     * Finds a district by its ID.
     *
     * @param id The ID of the district.
     * @return The district DTO if found, null otherwise.
     */
    override suspend fun findDistritoById(id: Short) = distritoRepository.findByIdOrNull(id)?.toDistritoDto()

    /**
     * Saves a new address entity using DTO.
     *
     * @param direccionDto The address DTO to save.
     * @return The saved address DTO.
     */
    override suspend fun saveDireccion(direccionDto: DireccionDto): DireccionDto {
        val direccionSaved: Direccion? = withTransaction { _ ->
            log.trace("Saving direccionDTO {}", direccionDto.toString())
            val direccion = direccionDto.toDireccion()
            log.trace("Converted direccion {}", direccion.toString())
            direccionRepository.persistAndFlush(direccion).awaitSuspending()
        }
        checkNotNull(direccionSaved) { "Direccion not saved" }
        return direccionSaved.toDireccionDto()
    }

    /**
     * Finds an address by its ID.
     *
     * @param id The ID of the address.
     * @return The address entity if found, null otherwise.
     */
    override suspend fun findDireccionById(id: UUID) = direccionRepository.findByIdOrNull(id)

    /**
     * Finds addresses by description and district ID.
     *
     * @param descripcion The description of the address.
     * @param distritoId The ID of the district.
     * @return A list of address entities.
     */
    override suspend fun findDireccionByDescripcionAndDistritoId(descripcion: String, distritoId: Short) =
        direccionRepository.findDireccionByDescripcionAndDistritoId(descripcion, distritoId)

    /**
     * Finds addresses by description and district name.
     *
     * @param descripcion The description of the address.
     * @param distritoNombre The name of the district.
     * @return A list of address entities.
     */
    override suspend fun findDireccionByDescripcionAndDistritoNombre(descripcion: String, distritoNombre: String) =
        direccionRepository.findDireccionByDescripcionAndDistritoNombre(descripcion, distritoNombre)

    /**
     * Finds addresses by description starting with a given string.
     *
     * @param descripcion The description prefix.
     * @return A list of address entities.
     */
    override suspend fun findDireccionByDescripcionStartingWith(descripcion: String) =
        direccionRepository.findDireccionByDescripcionStartingWith(descripcion)
}
