package io.github.kingg22.api.vacunas.panama.modules.direccion.extensions

import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DistritoDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.ProvinciaDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Distrito
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Provincia
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.toDistritoDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.toProvinciaDto

fun List<Distrito>.toListDistritoDto(): List<DistritoDto> = this.map { it.toDistritoDto() }

fun List<Provincia>.toListProvinciaDto(): List<ProvinciaDto> = this.map { it.toProvinciaDto() }
