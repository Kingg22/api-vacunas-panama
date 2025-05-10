package io.github.kingg22.api.vacunas.panama.modules.sede.entity

import io.github.kingg22.api.vacunas.panama.modules.sede.dto.SedeDto
import io.github.kingg22.api.vacunas.panama.modules.sede.dto.fromSede

fun Sede.toSedeDto() = SedeDto.fromSede(this)
