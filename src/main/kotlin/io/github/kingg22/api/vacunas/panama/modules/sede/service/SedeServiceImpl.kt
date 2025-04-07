package io.github.kingg22.api.vacunas.panama.modules.sede.service

import io.github.kingg22.api.vacunas.panama.modules.common.dto.UUIDNombreDto
import io.github.kingg22.api.vacunas.panama.modules.sede.repository.SedeRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SedeServiceImpl(private val sedeRepository: SedeRepository) : ISedeService {
    override fun getSedeById(id: UUID) = sedeRepository.findById(id)

    // TODO change to function
    override val idNombreSedes: List<UUIDNombreDto>
        @Cacheable(cacheNames = ["massive"], key = "'sedesNombre'")
        get() = sedeRepository.findAllIdAndNombre()
}
