package io.github.kingg22.api.vacunas.panama.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class NumDosisEnumConverter implements AttributeConverter<NumDosisEnum, String> {

    @Override
    public String convertToDatabaseColumn(NumDosisEnum attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public NumDosisEnum convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        dbData = dbData.trim().toUpperCase();
        return NumDosisEnum.fromValue(dbData);
    }
}
