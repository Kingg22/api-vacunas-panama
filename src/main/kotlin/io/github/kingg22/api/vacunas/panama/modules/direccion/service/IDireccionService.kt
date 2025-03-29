package io.github.kingg22.api.vacunas.panama.modules.direccion.service

import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DireccionDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DistritoDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.ProvinciaDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion
import jakarta.validation.Valid

interface IDireccionService {
    val distritosDto: List<DistritoDto>

    val provinciasDto: List<ProvinciaDto>

    fun createDireccion(direccionDto: @Valid DireccionDto): Direccion
}
