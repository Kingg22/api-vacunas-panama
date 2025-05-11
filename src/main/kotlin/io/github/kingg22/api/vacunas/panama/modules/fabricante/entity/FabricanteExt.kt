package io.github.kingg22.api.vacunas.panama.modules.fabricante.entity

import io.github.kingg22.api.vacunas.panama.modules.fabricante.dto.FabricanteDto
import io.github.kingg22.api.vacunas.panama.modules.fabricante.dto.fromFabricante

fun Fabricante.toFabricanteDto() = FabricanteDto.fromFabricante(this)
