package com.hwik.hcl.core.entity.enums

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class TransactionCategoryConverter : AttributeConverter<TransactionCategory, Int> {

    override fun convertToDatabaseColumn(status: TransactionCategory) : Int
    {
         return status.value
    }

    override fun convertToEntityAttribute(value: Int): TransactionCategory =
        TransactionCategory.values().firstOrNull { it.value == value }
            ?: throw IllegalArgumentException("Unknown value: $value")
}