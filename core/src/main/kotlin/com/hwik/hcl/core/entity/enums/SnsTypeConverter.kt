package com.hwik.hcl.core.entity.enums

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class SnsTypeConverter : AttributeConverter<SnsType, String> {

    override fun convertToDatabaseColumn(sns: SnsType) : String
    {
         return sns.value
    }

    override fun convertToEntityAttribute(value: String): SnsType =
        SnsType.values().firstOrNull { it.value == value }
            ?: throw IllegalArgumentException("Unknown value: $value")
}