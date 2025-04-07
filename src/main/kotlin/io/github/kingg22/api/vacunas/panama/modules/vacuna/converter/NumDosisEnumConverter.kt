package io.github.kingg22.api.vacunas.panama.modules.vacuna.converter

import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.NumDosisEnum
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class NumDosisEnumConverter : AttributeConverter<NumDosisEnum?, String?> {
    override fun convertToDatabaseColumn(attribute: NumDosisEnum?): String? = attribute.let {
        it?.value ?: return null
    }

    override fun convertToEntityAttribute(dbData: String?): NumDosisEnum? = dbData.let {
        if (it.isNullOrBlank()) null else NumDosisEnum.fromValue(it.trim().uppercase())
    }
}
