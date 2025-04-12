package io.github.kingg22.api.vacunas.panama.modules.direccion.service

import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DireccionDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Distrito
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
import java.util.Optional
import java.util.Optional.empty

@Service
class DireccionServiceImpl(
    private val direccionRepository: DireccionRepository,
    private val distritoRepository: DistritoRepository,
    private val provinciaRepository: ProvinciaRepository,
) : DireccionService {
    @Cacheable(cacheNames = ["massive"], key = "'distritosDto'", unless = "#result==null or #result.isEmpty()")
    @Transactional
    override fun getDistritosDto() = distritoRepository.findAll().toListDistritoDto()

    @Cacheable(cacheNames = ["massive"], key = "'provinciasDto'", unless = "#result==null or #result.isEmpty()")
    override fun getProvinciasDto() = provinciaRepository.findAll().toListProvinciaDto()

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun createDireccion(@Valid direccionDto: DireccionDto): Direccion {
        val distrito = direccionDto.distrito?.id?.let {
            distritoRepository.findById(it).orElseThrow()
        } ?: getDistritoDefault()

        return direccionRepository.save(
            Direccion(
                direccion = direccionDto.direccion,
                distrito = distrito,
                createdAt = direccionDto.createdAt,
            ),
        )
    }

    @Cacheable(cacheNames = ["massive"], key = "'direccionDefault'")
    override fun getDireccionDefault() = direccionRepository.findDireccionByDireccionAndDistrito_Id("Por registrar", 0)
        .orElseThrow()
        .first()

    @Cacheable(cacheNames = ["massive"], key = "'distritoDefault'")
    override fun getDistritoDefault(): Distrito = distritoRepository.findById(0).orElseThrow()

    override fun getDireccionByDto(@Valid direccionDto: DireccionDto): Optional<Direccion> {
        direccionDto.id?.let { return direccionRepository.findById(it) }

        val direccion = direccionDto.direccion.takeIf { it.isNotBlank() } ?: return empty()

        direccionDto.distrito?.id?.let { distritoId ->
            direccionRepository.findDireccionByDireccionAndDistrito_Id(direccion, distritoId.toInt()).get()
                .firstOrNull()?.let { return Optional.of(it) }
        }

        direccionDto.distrito?.nombre?.let { distritoNombre ->
            direccionRepository.findDireccionByDireccionAndDistrito_Nombre(direccion, distritoNombre).get()
                .firstOrNull()?.let { return Optional.of(it) }
        }

        direccionRepository.findDireccionByDireccionStartingWith(direccion.lowercase()).get()
            .firstOrNull()?.let { return Optional.of(it) }

        return runCatching { Optional.of(getDireccionDefault()) }.getOrElse { empty() }
    }
}
