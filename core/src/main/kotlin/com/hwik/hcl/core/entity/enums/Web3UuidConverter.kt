package com.hwik.hcl.core.entity.enums

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class Web3UuidConverter : AttributeConverter<Web3Uuid, String> {

    override fun convertToDatabaseColumn(uuid: Web3Uuid): String {
        return uuid.value
    }

    override fun convertToEntityAttribute(value: String): Web3Uuid =
        Web3Uuid.values().firstOrNull { it.value == value }
            ?: throw IllegalArgumentException("Unknown value: $value")
}