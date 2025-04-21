package io.github.kingg22.api.vacunas.panama.modules.vacuna.extensions

import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.NumDosisEnum
import io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.Dosis
import io.github.kingg22.api.vacunas.panama.modules.vacuna.entity.toDosisDto

/**
 * Converts a list of [Dosis] entities into a list of `DosisDto` objects.
 *
 * @return List of `DosisDto` mapped from this list of [Dosis].
 * @see io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.DosisDto
 */
fun List<Dosis>.toListDosisDto() = this.map { it.toDosisDto() }

fun String.getNumeroDosisAsEnum() = NumDosisEnum.fromValue(
    this.trim().uppercase()
        .also {
            require(it.isNotBlank())
        },
)
