package io.github.kingg22.api.vacunas.panama.persistence.entity.extensions

import io.github.kingg22.api.vacunas.panama.persistence.entity.Distrito
import io.github.kingg22.api.vacunas.panama.persistence.entity.Provincia
import io.github.kingg22.api.vacunas.panama.persistence.entity.toDistritoDto
import io.github.kingg22.api.vacunas.panama.persistence.entity.toProvinciaDto
import io.github.kingg22.api.vacunas.panama.web.dto.DistritoDto
import io.github.kingg22.api.vacunas.panama.web.dto.ProvinciaDto

fun List<Distrito>.toListDistritoDto(): List<DistritoDto> = this.map { it.toDistritoDto() }

fun List<Provincia>.toListProvinciaDto(): List<ProvinciaDto> = this.map { it.toProvinciaDto() }
