package com.hwik.hcl.user.service

import com.hwik.hcl.core.entity.TransactionEntity
import com.hwik.hcl.core.entity.UserEntity
import com.hwik.hcl.core.entity.enums.TransactionCategory
import com.hwik.hcl.core.repository.TransactionRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
) {
    fun getTransactionList(page: Int, user: UserEntity): Page<TransactionEntity> {
        val pr = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"))

        val transactionList = transactionRepository.findByUserIdAndCategoryIn(user.id!!, TransactionCategory.toListForUser(), pr);

        return transactionList
    }
}