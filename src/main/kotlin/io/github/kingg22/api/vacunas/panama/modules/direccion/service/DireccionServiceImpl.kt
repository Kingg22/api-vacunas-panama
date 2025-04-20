package io.github.kingg22.api.vacunas.panama.modules.direccion.service

import io.github.kingg22.api.vacunas.panama.configuration.CacheDuration
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DireccionDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DistritoDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.toDistrito
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.toDireccionDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.toDistritoDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.extensions.toListDistritoDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.extensions.toListProvinciaDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.repository.DireccionRepository
import io.github.kingg22.api.vacunas.panama.modules.direccion.repository.DistritoRepository
import io.github.kingg22.api.vacunas.panama.modules.direccion.repository.ProvinciaRepository
import jakarta.validation.Valid
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class DireccionServiceImpl(
    private val direccionRepository: DireccionRepository,
    private val distritoRepository: DistritoRepository,
    private val provinciaRepository: ProvinciaRepository,
) : DireccionService {
    @Cacheable(
        cacheNames = [CacheDuration.MASSIVE_VALUE],
        key = "'distritosDto'",
        unless = "#result==null or #result.isEmpty()",
    )
    @Transactional
    override fun getDistritosDto() = distritoRepository.findAll().toListDistritoDto()

    @Cacheable(
        cacheNames = [CacheDuration.MASSIVE_VALUE],
        key = "'provinciasDto'",
        unless = "#result==null or #result.isEmpty()",
    )
    override fun getProvinciasDto() = provinciaRepository.findAll().toListProvinciaDto()

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun createDireccion(@Valid direccionDto: DireccionDto): DireccionDto {
        val distrito = direccionDto.distrito.id?.let {
            distritoRepository.findById(it).orElseThrow()
        } ?: getDistritoDefault().toDistrito()

        return direccionRepository.save(
            Direccion(
                descripcion = direccionDto.descripcion,
                distrito = distrito,
                createdAt = direccionDto.createdAt,
            ),
        ).toDireccionDto()
    }

    @Cacheable(cacheNames = [CacheDuration.MASSIVE_VALUE], key = "'direccionDefault'")
    override fun getDireccionDefault() =
        direccionRepository.findDireccionByDescripcionAndDistrito_Id("Por registrar", 0).firstOrNull()?.toDireccionDto()
            ?: throw IllegalStateException("Dirección default not found")

    @Cacheable(cacheNames = [CacheDuration.MASSIVE_VALUE], key = "'distritoDefault'")
    override fun getDistritoDefault(): DistritoDto = distritoRepository.findByIdOrNull(0)?.toDistritoDto()
        ?: throw IllegalStateException("Distrito default not found")

    override fun getDireccionByDto(@Valid direccionDto: DireccionDto): DireccionDto? {
        direccionDto.id?.let { return direccionRepository.findByIdOrNull(it)?.toDireccionDto() }

        val direccion = direccionDto.descripcion.takeIf { it.isNotBlank() } ?: return null

        direccionDto.distrito.id?.let { distritoId ->
            direccionRepository.findDireccionByDescripcionAndDistrito_Id(
                direccion,
                distritoId.toInt(),
            ).firstOrNull()?.let {
                return it.toDireccionDto()
            }
        }

        direccionDto.distrito.nombre.let { distritoNombre ->
            direccionRepository.findDireccionByDescripcionAndDistrito_Nombre(
                direccion,
                distritoNombre,
            ).firstOrNull()?.let {
                return it.toDireccionDto()
            }
        }

        direccionRepository.findDireccionByDescripcionStartingWith(direccion.lowercase()).firstOrNull()?.let {
            return it.toDireccionDto()
        }

        return null
    }
}
