package io.github.kingg22.api.vacunas.panama.util

enum class RolesEnum(val priority: Int) {
    PACIENTE(1),
    FABRICANTE(2),
    ENFERMERA(3),
    DOCTOR(4),
    ADMINISTRATIVO(5),
    DEVELOPER(6),
    AUTORIDAD(7),
    ;

    companion object {
        @JvmStatic
        fun getByPriority(priority: Int): RolesEnum = entries.find { it.priority == priority }.let {
            it?.let { return it }
            throw IllegalArgumentException("No Roles Enum found with priority $priority")
        }
    }
}
