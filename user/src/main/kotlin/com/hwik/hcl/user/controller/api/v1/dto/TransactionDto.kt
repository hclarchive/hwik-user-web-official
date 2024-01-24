package com.hwik.hcl.user.controller.api.v1.dto

import com.hwik.hcl.core.entity.QuizEntity
import com.hwik.hcl.core.entity.TransactionEntity
import com.hwik.hcl.core.entity.UserEntity
import com.hwik.hcl.core.entity.enums.StatusType
import com.hwik.hcl.core.entity.enums.StatusTypeConverter
import com.hwik.hcl.core.entity.enums.TransactionCategory
import jakarta.persistence.*
import org.modelmapper.ModelMapper
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.math.BigDecimal
import java.time.Instant

data class TransactionDto (
    var id: Int? = null,
    var category: String? = null,
    var title: String? = null,
    var fromAddress: String? = null,
    var toAddress: String? = null,
    var hash: String? = null,
    var amount: BigDecimal? = null,
    var symbol: String? = null,
    var fee: BigDecimal? = null,
    var explorer: String? = null,
    var status: StatusType? = null,
    var updatedAt: Instant? = null,
    var createdAt: Instant? = null,
)

fun TransactionEntity.toDto(modelMapper : ModelMapper) : TransactionDto
{
    val dto = modelMapper.map(this, TransactionDto::class.java)
    return dto
}
