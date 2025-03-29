package io.github.kingg22.api.vacunas.panama.service

import io.github.kingg22.api.vacunas.panama.persistence.entity.Direccion
import io.github.kingg22.api.vacunas.panama.web.dto.DireccionDto
import io.github.kingg22.api.vacunas.panama.web.dto.DistritoDto
import io.github.kingg22.api.vacunas.panama.web.dto.ProvinciaDto
import jakarta.validation.Valid

interface IDireccionService {
    val distritosDto: List<DistritoDto>

    val provinciasDto: List<ProvinciaDto>

    fun createDireccion(direccionDto: @Valid DireccionDto): Direccion
}
