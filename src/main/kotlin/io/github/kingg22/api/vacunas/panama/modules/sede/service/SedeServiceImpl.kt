package io.github.kingg22.api.vacunas.panama.modules.sede.service

import io.github.kingg22.api.vacunas.panama.configuration.CacheDuration
import io.github.kingg22.api.vacunas.panama.modules.sede.entity.toSedeDto
import io.github.kingg22.api.vacunas.panama.modules.sede.repository.SedeRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SedeServiceImpl(private val sedeRepository: SedeRepository) : SedeService {
    @Deprecated("Use DTO instead", replaceWith = ReplaceWith("getSedeDtoById(id)"))
    override fun getSedeById(id: UUID) = sedeRepository.findByIdOrNull(id)

    override fun getSedeDtoById(id: UUID) = sedeRepository.findByIdOrNull(id)?.toSedeDto()

    @Cacheable(
        cacheNames = [CacheDuration.MASSIVE_VALUE],
        key = "'sedesNombre'",
        unless = "#result==null or #result.isEmpty()",
    )
    override fun getIdNombreSedes() = sedeRepository.findAllIdAndNombre()
}
