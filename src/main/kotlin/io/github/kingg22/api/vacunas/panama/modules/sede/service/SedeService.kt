package io.github.kingg22.api.vacunas.panama.modules.sede.service

import io.github.kingg22.api.vacunas.panama.modules.common.dto.UUIDNombreDto
import io.github.kingg22.api.vacunas.panama.modules.sede.dto.SedeDto
import java.util.Optional
import java.util.UUID

interface SedeService {
    fun getIdNombreSedes(): List<UUIDNombreDto>

    fun getSedeById(id: UUID): Optional<SedeDto>
}
