package com.hwik.hcl.user.controller.api.v1

import com.hwik.hcl.core.entity.UserEntity
import com.hwik.hcl.core.exception.HclException
import com.hwik.hcl.user.service.Web3Service
import com.hwik.hcl.user.controller.api.v1.dto.TelegramDto
import com.hwik.hcl.user.controller.api.v1.dto.TransactionDto
import com.hwik.hcl.user.controller.api.v1.dto.toDto
import com.hwik.hcl.user.util.RequestUtil
import com.hwik.hcl.user.util.UserUtil
import org.modelmapper.ModelMapper
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/api/v1/web3")
@RequestMapping("/api/v1/web3")
class Web3Controller(
    private val web3Service: Web3Service,
    private val userUtil: UserUtil,
    private val modelMapper: ModelMapper,
    private val requestUtil: RequestUtil,
) {
    @PostMapping("/{id}/claim")
    fun claim(
        @PathVariable id: Long,
    ): ResponseEntity<TransactionDto> {
        val user: UserEntity? = userUtil.getUser()
        if (user == null) {
            return ResponseEntity.badRequest().build()
        }

        val tx = web3Service.claim(user, id)
        val dto = tx.toDto(modelMapper)

        return ResponseEntity.ok(dto)
    }

    @PostMapping("/sns/telegram")
    fun signTelegram(
        @RequestBody telegramDto: TelegramDto,
    ): ResponseEntity<String> {
        try {
            val user: UserEntity = userUtil.getUser() ?: throw HclException("not found user")
            web3Service.signTelegram(user, telegramDto.authResult)

            return ResponseEntity.ok().build()
        } catch (e: Exception) {
            return ResponseEntity.ok(e.message)
        }
    }

    @GetMapping("/{id}/popupurl")
    fun getPopupUrl(
        @PathVariable id: Long,
    ): ResponseEntity<String> {
        val user: UserEntity? = userUtil.getUser()

        if (user == null) {
            return ResponseEntity.badRequest().build()
        }

        val domain = requestUtil.getDomain()
        val url = web3Service.getPopupUrl(user, id, domain)
        if (url == null) {
            return ResponseEntity.badRequest().build()
        }

        return ResponseEntity.ok(url)
    }
}