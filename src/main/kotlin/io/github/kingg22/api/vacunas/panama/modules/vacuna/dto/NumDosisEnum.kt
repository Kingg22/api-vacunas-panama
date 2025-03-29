package io.github.kingg22.api.vacunas.panama.modules.vacuna.dto

import java.io.Serializable

enum class NumDosisEnum(val value: String) : Serializable {
    DOSIS_PREVIA("P"),
    PRIMERA_DOSIS("1"),
    SEGUNDA_DOSIS("2"),
    TERCERA_DOSIS("3"),
    REFUERZO("R"),
    PRIMER_REFUERZO("R1"),
    SEGUNDO_REFUERZO("R2"),
    ;

    fun isValidNew(newDosis: NumDosisEnum): Boolean = when (this) {
        DOSIS_PREVIA -> newDosis == PRIMERA_DOSIS
        PRIMERA_DOSIS -> newDosis in setOf(SEGUNDA_DOSIS, REFUERZO, PRIMER_REFUERZO)
        SEGUNDA_DOSIS -> newDosis in setOf(TERCERA_DOSIS, REFUERZO, PRIMER_REFUERZO)
        TERCERA_DOSIS -> newDosis in setOf(REFUERZO, PRIMER_REFUERZO)
        REFUERZO -> newDosis in setOf(PRIMER_REFUERZO, REFUERZO)
        PRIMER_REFUERZO -> newDosis == SEGUNDO_REFUERZO
        SEGUNDO_REFUERZO -> false
    }

    companion object {
        @JvmStatic
        fun fromValue(value: String): NumDosisEnum = entries.find { it.value == value }.let {
            it?.let { return it }
            throw IllegalArgumentException("No enum constant founded for: $value")
        }
    }
}
