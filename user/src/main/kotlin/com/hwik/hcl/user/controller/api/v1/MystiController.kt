package com.hwik.hcl.user.controller.api.v1

import com.hwik.hcl.core.entity.MystiboxEntity
import com.hwik.hcl.core.entity.enums.StatusType
import com.hwik.hcl.user.service.MystiboxService
import com.hwik.hcl.user.controller.api.v1.dto.MystiboxDto
import com.hwik.hcl.user.controller.api.v1.dto.TransactionDto
import com.hwik.hcl.user.controller.api.v1.dto.toDto
import com.hwik.hcl.user.util.RequestUtil
import com.hwik.hcl.user.util.UserUtil
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController("/api/v1/mysti")
@RequestMapping("/api/v1/mysti")
class MystiController(
    private val mystiboxService: MystiboxService,
    private val modelMapper: ModelMapper,
    private val userUtil: UserUtil,
    private val requestUtil: RequestUtil
) {
    @GetMapping("")
    fun getMystiList(): ResponseEntity<Page<MystiboxDto>> {
        val mystiboxList : Page<MystiboxDto> = mystiboxService.getList(0, 3, StatusType.toListAll()).map {
            val dto = it.toDto(modelMapper, requestUtil.getContextPath())

            dto
        }

        return ResponseEntity.ok(mystiboxList)
    }

    @GetMapping("/history")
    fun getHistory(
        @RequestParam(value = "page", required = false, defaultValue = "0") page: Int,
        @RequestParam(value = "size", required = false, defaultValue = "10") size: Int,
    ): ResponseEntity<Page<MystiboxDto>> {
        val user = userUtil.getUser() ?: throw Exception("user is invalid")

        val mystiboxPage: Page<MystiboxEntity> = mystiboxService.getParticipantHistory(user, page, size)

        val mystiboxDtoPage: Page<MystiboxDto> = mystiboxPage.map {
            it.toDto(modelMapper, requestUtil.getContextPath())
        }

        return ResponseEntity.ok(mystiboxDtoPage)
    }

    @GetMapping("/{id}/coupon")
    fun getCoupon(
        @PathVariable id: Long,
    ): ResponseEntity<String> {
        val user = userUtil.getUser() ?: throw Exception("user is invalid")

        val winnerAddress = mystiboxService.getWinnerAddress(id);
        if (user.address != winnerAddress) {
            return ResponseEntity.badRequest().build()
        }

        val mystibox = mystiboxService.get(id)
        if (mystibox.coupon.isNullOrEmpty()) {
            return ResponseEntity.ok("Please wait for the coupon to be issued.");
        }

        return ResponseEntity.ok(mystibox.coupon)
    }


    @GetMapping("/{id}/entry")
    fun checkMystiEntry(
        @PathVariable id: String,
    ): ResponseEntity<Boolean> {
        val user = userUtil.getUser() ?: throw Exception("user is invalid")

        val res = mystiboxService.checkEntry(user.id!!, id.toLong())

        if (res)
        {
            return ResponseEntity.ok(res)
        }

        return ResponseEntity.badRequest().build()
    }

    @PostMapping("/{id}/entry")
    fun postMystiEntry(
        @PathVariable id: String,
        @RequestBody dto: TransactionDto,
    ): ResponseEntity<TransactionDto> {
        val hash = dto.hash ?: throw Exception("hash is invalid")
        val user = userUtil.getUser() ?: throw Exception("user is invalid")

        val tx = mystiboxService.entry(user.id!!, id.toLong(), hash)

        if (tx == null) {
            return ResponseEntity.badRequest().build()
        }

        val txDto = modelMapper.map(tx, TransactionDto::class.java)
        return ResponseEntity.ok(txDto)
    }
}