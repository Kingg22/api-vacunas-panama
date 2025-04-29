package io.github.kingg22.api.vacunas.panama.modules.sede.service

import io.github.kingg22.api.vacunas.panama.configuration.CacheDuration
import io.github.kingg22.api.vacunas.panama.modules.sede.dto.toSedeModel
import io.github.kingg22.api.vacunas.panama.modules.sede.entity.toSedeDto
import io.github.kingg22.api.vacunas.panama.modules.sede.persistence.SedePersistenceService
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SedeServiceImpl(private val sedePersistenceService: SedePersistenceService) : SedeService {
    override suspend fun getSedeById(id: UUID) = sedePersistenceService.findSedeById(id)?.toSedeDto()?.toSedeModel()

    @Cacheable(
        cacheNames = [CacheDuration.MASSIVE_VALUE],
        key = "'sedesNombre'",
        unless = "#result==null or #result.isEmpty()",
    )
    override suspend fun getIdNombreSedes() = sedePersistenceService.findAllIdAndNombre()
}
