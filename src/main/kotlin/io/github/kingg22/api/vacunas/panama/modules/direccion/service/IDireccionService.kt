package io.github.kingg22.api.vacunas.panama.modules.direccion.service

import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DireccionDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DistritoDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.ProvinciaDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Distrito
import jakarta.validation.Valid
import java.util.Optional

interface IDireccionService {
    val distritosDto: List<DistritoDto>

    val provinciasDto: List<ProvinciaDto>

    fun createDireccion(direccionDto: @Valid DireccionDto): Direccion

    fun getDireccionDefault(): Direccion

    fun getDistritoDefault(): Distrito

    /**
     * Busca una dirección basada en el DTO proporcionado.
     * Devuelve un Optional con la dirección si se encuentra, o vacío si no se encuentra.
     *
     * @param direccionDto DTO con los datos para buscar la dirección
     * @return Optional con la dirección encontrada o vacío
     */
    fun getDireccionByDto(direccionDto: DireccionDto): Optional<Direccion>
}
