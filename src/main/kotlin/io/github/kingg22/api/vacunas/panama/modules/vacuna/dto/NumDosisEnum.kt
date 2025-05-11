package io.github.kingg22.api.vacunas.panama.modules.vacuna.dto

import com.fasterxml.jackson.annotation.JsonAlias
import io.quarkus.runtime.annotations.RegisterForReflection
import java.io.Serializable

@RegisterForReflection
enum class NumDosisEnum(val value: String) : Serializable {
    // TODO find if have solution to apply @all:
    @field:JsonAlias("P", "p", "previa", "previous")
    DOSIS_PREVIA("P"),

    @field:JsonAlias("1", "first", "one", "primera")
    PRIMERA_DOSIS("1"),

    @field:JsonAlias("2", "second", "two", "segunda")
    SEGUNDA_DOSIS("2"),

    @field:JsonAlias("3", "third", "three", "tercera")
    TERCERA_DOSIS("3"),

    @field:JsonAlias("R", "r", "refuerzo", "refill")
    REFUERZO("R"),

    @field:JsonAlias("R1", "r1", "refuerzo_1", "refill_1", "first_refill")
    PRIMER_REFUERZO("R1"),

    @field:JsonAlias("R2", "r2", "refuerzo_2", "refill_2", "second_refill")
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
        fun fromValue(value: String): NumDosisEnum = value.let { valueParam ->
            val value = valueParam.trim().uppercase()
            require(value.isNotBlank())
            entries.find { it.value == value }.let {
                it?.let { return it }
                throw IllegalArgumentException("No enum constant founded for: $value")
            }
        }
    }
}
