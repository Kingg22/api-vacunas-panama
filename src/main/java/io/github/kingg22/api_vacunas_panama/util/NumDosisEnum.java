package io.github.kingg22.api_vacunas_panama.util;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum NumDosisEnum implements Serializable {
    DOSIS_PREVIA("P"),
    PRIMERA_DOSIS("1"),
    SEGUNDA_DOSIS("2"),
    TERCERA_DOSIS("3"),
    REFUERZO("R"),
    PRIMER_REFUERZO("R1"),
    SEGUNDO_REFUERZO("R2");

    private final String value;

    NumDosisEnum(String value) {
        this.value = value;
    }

    public boolean isValidNew(NumDosisEnum newDosis) {
        switch (this) {
            case DOSIS_PREVIA -> {
                return newDosis == PRIMERA_DOSIS;
            }
            case PRIMERA_DOSIS -> {
                return newDosis == SEGUNDA_DOSIS || newDosis == REFUERZO || newDosis == PRIMER_REFUERZO;
            }
            case SEGUNDA_DOSIS -> {
                return newDosis == TERCERA_DOSIS || newDosis == REFUERZO || newDosis == PRIMER_REFUERZO;
            }
            case TERCERA_DOSIS -> {
                return newDosis == REFUERZO || newDosis == PRIMER_REFUERZO;
            }
            case REFUERZO -> {
                return newDosis == PRIMER_REFUERZO || newDosis == REFUERZO;
            }
            case PRIMER_REFUERZO -> {
                return newDosis == SEGUNDO_REFUERZO;
            }
            default -> {
                return false;
            }
        }
    }

    public static NumDosisEnum fromValue(String value) {
        for (NumDosisEnum numDosisEnum : NumDosisEnum.values()) {
            if (numDosisEnum.value.equals(value)) {
                return numDosisEnum;
            }
        }
        throw new IllegalArgumentException("No enum constant founded for: " + value);
    }

}
