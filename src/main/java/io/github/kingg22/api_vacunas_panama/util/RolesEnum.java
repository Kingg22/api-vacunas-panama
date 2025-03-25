package io.github.kingg22.api_vacunas_panama.util;

import lombok.Getter;

@Getter
public enum RolesEnum {
    PACIENTE(1),
    FABRICANTE(2),
    ENFERMERA(3),
    DOCTOR(4),
    ADMINISTRATIVO(5),
    DEVELOPER(6),
    AUTORIDAD(7);

    private final int priority;

    RolesEnum(int priority) {
        this.priority = priority;
    }

    public static RolesEnum getByPriority(int priority) {
        for (RolesEnum role : RolesEnum.values()) {
            if (role.priority == priority) {
                return role;
            }
        }
        throw new IllegalArgumentException("No Roles Enum found with priority " + priority);
    }

}
