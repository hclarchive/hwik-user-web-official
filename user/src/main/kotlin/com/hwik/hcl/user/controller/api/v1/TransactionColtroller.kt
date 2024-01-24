package com.hwik.hcl.user.controller.api.v1

import com.hwik.hcl.core.exception.HclException
import com.hwik.hcl.user.service.TransactionService
import com.hwik.hcl.user.controller.api.v1.dto.TransactionDto
import com.hwik.hcl.user.controller.dto.QuizDto
import com.hwik.hcl.user.util.UserUtil
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/api/v1/transaction")
@RequestMapping("/api/v1/transaction")
class TransactionColtroller(
    private val transactionService: TransactionService,
    private val modelMapper: ModelMapper,
    private val userUtil: UserUtil
) {
    @GetMapping
    fun getTransactionList(page: Int?): Page<TransactionDto> {
        val tmpPage = page ?: 0
        val user = userUtil.getUser() ?: throw HclException("login required")

        val txLsit = transactionService.getTransactionList(tmpPage, user)

        return txLsit.map { modelMapper.map(it, TransactionDto::class.java) }
    }
}
