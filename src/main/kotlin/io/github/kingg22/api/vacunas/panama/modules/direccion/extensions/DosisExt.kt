package io.github.kingg22.api.vacunas.panama.modules.direccion.extensions

import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.DosisDto
import io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.Dosis
import io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.toDosisDto

fun List<Dosis>.toListDosisDto(): List<DosisDto> = this.map { it.toDosisDto() }
