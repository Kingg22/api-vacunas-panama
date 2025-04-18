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
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

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
        val distrito = direccionDto.distrito?.id?.let {
            distritoRepository.findById(it).orElseThrow()
        } ?: getDistritoDefault().toDistrito()

        return direccionRepository.save(
            Direccion(
                direccion = direccionDto.direccion,
                distrito = distrito,
                createdAt = direccionDto.createdAt,
            ),
        ).toDireccionDto()
    }

    @Cacheable(cacheNames = [CacheDuration.MASSIVE_VALUE], key = "'direccionDefault'")
    override fun getDireccionDefault() =
        direccionRepository.findDireccionByDireccionAndDistrito_Id("Por registrar", 0)?.first()?.toDireccionDto()
            ?: throw IllegalStateException("Dirección default not found")

    @Cacheable(cacheNames = [CacheDuration.MASSIVE_VALUE], key = "'distritoDefault'")
    override fun getDistritoDefault(): DistritoDto =
        distritoRepository.findById(0).map { it.toDistritoDto() }.orElseThrow()

    override fun getDireccionByDto(@Valid direccionDto: DireccionDto): DireccionDto? {
        direccionDto.id?.let { return direccionRepository.findById(it).getOrNull()?.toDireccionDto() }

        val direccion = direccionDto.direccion.takeIf { it.isNotBlank() } ?: return null

        direccionDto.distrito?.id?.let { distritoId ->
            direccionRepository.findDireccionByDireccionAndDistrito_Id(
                direccion,
                distritoId.toInt(),
            )?.firstOrNull()?.let {
                return it.toDireccionDto()
            }
        }

        direccionDto.distrito?.nombre?.let { distritoNombre ->
            direccionRepository.findDireccionByDireccionAndDistrito_Nombre(
                direccion,
                distritoNombre,
            )?.firstOrNull()?.let {
                return it.toDireccionDto()
            }
        }

        direccionRepository.findDireccionByDireccionStartingWith(direccion.lowercase())?.firstOrNull()?.let {
            return it.toDireccionDto()
        }

        return null
    }
}
