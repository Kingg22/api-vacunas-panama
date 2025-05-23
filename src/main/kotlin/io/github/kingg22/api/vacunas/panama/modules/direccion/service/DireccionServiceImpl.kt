package io.github.kingg22.api.vacunas.panama.modules.direccion.service

import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DireccionDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.toDireccionDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.persistence.DireccionPersistenceService
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class DireccionServiceImpl(private val direccionPersistenceService: DireccionPersistenceService) : DireccionService {

    /*
    @Cacheable(
        cacheNames = [CacheDuration.MASSIVE_VALUE],
        key = "'distritosDto'",
        unless = "#result==null or #result.isEmpty()",
    )
    @CacheResult(cacheName = CacheDuration.MASSIVE_VALUE)
     */
    override suspend fun getDistritosDto() = direccionPersistenceService.findAllDistritos()

    /*
    @Cacheable(
        cacheNames = [CacheDuration.MASSIVE_VALUE],
        key = "'provinciasDto'",
        unless = "#result==null or #result.isEmpty()",
    )
    @CacheResult(cacheName = CacheDuration.MASSIVE_VALUE)
     */
    override suspend fun getProvinciasDto() = direccionPersistenceService.findAllProvincias()

    // TODO add transaction propagation requires new
    override suspend fun createDireccion(direccionDto: DireccionDto): DireccionDto {
        val distritoDto = direccionDto.distrito.id?.let {
            direccionPersistenceService.findDistritoById(it)
        } ?: getDistritoDefault()

        return direccionPersistenceService.saveDireccion(
            direccionDto.copy(
                id = null,
                updatedAt = null,
                distrito = distritoDto,
            ),
        )
    }

    suspend fun getDistritoDefault() = direccionPersistenceService.findDistritoById(0)
        ?: throw IllegalStateException("Distrito default not found")

    override suspend fun getDireccionByDto(direccionDto: DireccionDto): DireccionDto? {
        direccionDto.id?.let { return direccionPersistenceService.findDireccionById(it)?.toDireccionDto() }

        val direccion = direccionDto.descripcion.takeIf { it.isNotBlank() } ?: return null

        direccionDto.distrito.id?.let { distritoId ->
            direccionPersistenceService.findDireccionByDescripcionAndDistritoId(
                direccion,
                distritoId,
            ).firstOrNull()?.let {
                return it.toDireccionDto()
            }
        }

        direccionDto.distrito.nombre.let { distritoNombre ->
            direccionPersistenceService.findDireccionByDescripcionAndDistritoNombre(
                direccion,
                distritoNombre,
            ).firstOrNull()?.let {
                return it.toDireccionDto()
            }
        }

        return direccionPersistenceService.findDireccionByDescripcionStartingWith(direccion.lowercase())
            .firstOrNull()
            ?.let {
                return it.toDireccionDto()
            }
    }
}
