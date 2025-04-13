package io.github.kingg22.api.vacunas.panama.modules.sede.service

import io.github.kingg22.api.vacunas.panama.configuration.CacheDuration
import io.github.kingg22.api.vacunas.panama.modules.sede.repository.SedeRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SedeServiceImpl(private val sedeRepository: SedeRepository) : SedeService {
    override fun getSedeById(id: UUID) = sedeRepository.findById(id)

    @Cacheable(
        cacheNames = [CacheDuration.MASSIVE_VALUE],
        key = "'sedesNombre'",
        unless = "#result==null or #result.isEmpty()",
    )
    override fun getIdNombreSedes() = sedeRepository.findAllIdAndNombre()
}
