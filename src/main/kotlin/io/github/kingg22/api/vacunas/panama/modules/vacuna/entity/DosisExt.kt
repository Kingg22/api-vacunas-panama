package io.github.kingg22.api.vacunas.panama.modules.vacuna.entity

import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.DosisDto
import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.fromDosis

fun Dosis.toDosisDto() = DosisDto.fromDosis(this)

/**
 * Converts a list of [Dosis] entities into a list of [DosisDto] objects.
 *
 * @return List of [DosisDto] mapped from this list of [Dosis].
 */
fun List<Dosis>.toListDosisDto() = this.map { DosisDto.fromDosis(it) }
