package io.github.kingg22.api.vacunas.panama.persistence.entity.extensions

import io.github.kingg22.api.vacunas.panama.persistence.entity.Dosis
import io.github.kingg22.api.vacunas.panama.persistence.entity.toDosisDto
import io.github.kingg22.api.vacunas.panama.web.dto.DosisDto

fun List<Dosis>.toListDosisDto(): List<DosisDto> = this.map { it.toDosisDto() }
