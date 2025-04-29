package io.github.kingg22.api.vacunas.panama.modules.direccion.persistence

import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.toDistritoDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.extensions.toListDistritoDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.extensions.toListProvinciaDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.repository.DireccionRepository
import io.github.kingg22.api.vacunas.panama.modules.direccion.repository.DistritoRepository
import io.github.kingg22.api.vacunas.panama.modules.direccion.repository.ProvinciaRepository
import jakarta.persistence.EntityManager
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import java.util.UUID

/**
 * Implementation of the DireccionPersistenceService interface.
 *
 * This service provides operations related to the persistence of addresses,
 * acting as an intermediate layer between the repositories and the service layer.
 * It encapsulates all JPA-related operations.
 */
@Service
class DireccionPersistenceServiceImpl(
    private val entityManager: EntityManager,
    private val transactionTemplate: TransactionTemplate,
    private val direccionRepository: DireccionRepository,
    private val distritoRepository: DistritoRepository,
    private val provinciaRepository: ProvinciaRepository,
) : DireccionPersistenceService {

    /**
     * Finds all districts.
     *
     * @return A list of district DTOs.
     */
    override suspend fun findAllDistritos() = distritoRepository.findAll().toListDistritoDto()

    /**
     * Finds all provinces.
     *
     * @return A list of province DTOs.
     */
    override suspend fun findAllProvincias() = provinciaRepository.findAll().toListProvinciaDto()

    /**
     * Finds a district by its ID.
     *
     * @param id The ID of the district.
     * @return The district DTO if found, null otherwise.
     */
    override suspend fun findDistritoById(id: Short) = distritoRepository.findByIdOrNull(id)?.toDistritoDto()

    /**
     * Saves a new address entity.
     *
     * @param direccion The address entity to save.
     * @return The saved address entity.
     */
    override suspend fun saveDireccion(direccion: Direccion): Direccion {
        var direccionSaved: Direccion? = null
        transactionTemplate.execute {
            entityManager.merge(direccion)
            direccionSaved = direccionRepository.save(direccion)
        }
        checkNotNull(direccionSaved) { "Direccion not saved" }
        return direccionSaved
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
    override suspend fun findDireccionByDescripcionAndDistritoId(descripcion: String, distritoId: Int) =
        direccionRepository.findDireccionByDescripcionAndDistrito_Id(descripcion, distritoId)

    /**
     * Finds addresses by description and district name.
     *
     * @param descripcion The description of the address.
     * @param distritoNombre The name of the district.
     * @return A list of address entities.
     */
    override suspend fun findDireccionByDescripcionAndDistritoNombre(descripcion: String, distritoNombre: String) =
        direccionRepository.findDireccionByDescripcionAndDistrito_Nombre(descripcion, distritoNombre)

    /**
     * Finds addresses by description starting with a given string.
     *
     * @param descripcion The description prefix.
     * @return A list of address entities.
     */
    override suspend fun findDireccionByDescripcionStartingWith(descripcion: String) =
        direccionRepository.findDireccionByDescripcionStartingWith(descripcion)
}
