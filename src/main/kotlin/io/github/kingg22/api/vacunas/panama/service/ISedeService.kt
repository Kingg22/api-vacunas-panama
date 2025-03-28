package io.github.kingg22.api.vacunas.panama.service

import io.github.kingg22.api.vacunas.panama.persistence.entity.Sede
import io.github.kingg22.api.vacunas.panama.web.dto.UUIDNombreDto
import java.util.Optional
import java.util.UUID

interface ISedeService {
    val idNombreSedes: List<UUIDNombreDto>

    fun getSedeById(id: UUID): Optional<Sede>
}
