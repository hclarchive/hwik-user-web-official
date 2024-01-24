package com.hwik.hcl.core.entity.enums

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class StatusTypeConverter : AttributeConverter<StatusType, String> {

    override fun convertToDatabaseColumn(status: StatusType) : String
    {
         return status.value
    }

    override fun convertToEntityAttribute(value: String): StatusType =
        StatusType.values().firstOrNull { it.value == value }
            ?: throw IllegalArgumentException("Unknown value: $value")
}