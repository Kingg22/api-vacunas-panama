package io.github.kingg22.api.vacunas.panama.modules.direccion.extensions

import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Distrito
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Provincia
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.toDistritoDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.toProvinciaDto

/**
 * Converts a list of [Distrito] entities into a list of `DistritoDto` objects.
 *
 * @return List of `DistritoDto` mapped from this list of [Distrito].
 * @see io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DistritoDto
 */
fun List<Distrito>.toListDistritoDto() = this.map { it.toDistritoDto() }

/**
 * Converts a list of [Provincia] entities into a list of `ProvinciaDto` objects.
 *
 * @return List of `ProvinciaDto` mapped from this list of [Provincia].
 * @see io.github.kingg22.api.vacunas.panama.modules.direccion.dto.ProvinciaDto
 */
fun List<Provincia>.toListProvinciaDto() = this.map { it.toProvinciaDto() }
