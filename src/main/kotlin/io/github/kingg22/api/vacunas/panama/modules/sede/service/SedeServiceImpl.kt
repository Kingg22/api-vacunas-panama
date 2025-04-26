package io.github.kingg22.api.vacunas.panama.modules.sede.service

import io.github.kingg22.api.vacunas.panama.configuration.CacheDuration
import io.github.kingg22.api.vacunas.panama.modules.sede.repository.SedeRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SedeServiceImpl(private val sedeRepository: SedeRepository) : SedeService {
    @Deprecated("Use DTO instead")
    override suspend fun getSedeById(id: UUID) = sedeRepository.findByIdOrNull(id)

    @Cacheable(
        cacheNames = [CacheDuration.MASSIVE_VALUE],
        key = "'sedesNombre'",
        unless = "#result==null or #result.isEmpty()",
    )
    override suspend fun getIdNombreSedes() = sedeRepository.findAllIdAndNombre()
}
