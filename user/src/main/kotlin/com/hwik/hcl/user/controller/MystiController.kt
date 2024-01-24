package com.hwik.hcl.user.controller

import com.hwik.hcl.core.entity.enums.StatusType
import com.hwik.hcl.user.service.MystiboxService
import com.hwik.hcl.core.util.Web3jUtil
import com.hwik.hcl.user.controller.dto.toDto
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import com.hwik.hcl.user.util.RequestUtil
import org.springframework.web.bind.annotation.PathVariable

@Controller
@RequestMapping("/mysti")
class MystiController(
    private val mystiboxService: MystiboxService,
    private val modelMapper: ModelMapper,
    private val requestUtil: RequestUtil,
    private val web3jUtil: Web3jUtil
) {
    @GetMapping
    fun index(
        model: Model
    ): String {
        model.addAttribute("menu", "mysti");

        val goingList = mystiboxService.getList(0, 3, listOf(StatusType.Ing, StatusType.Waiting, StatusType.Pending)).map {
            it.toDto(modelMapper, requestUtil.getContextPath())
        }

        val endedList = mystiboxService.getList(0, 3, listOf(StatusType.Ended, StatusType.Success)).map {
            it.toDto(modelMapper, requestUtil.getContextPath())
        }

        model.addAttribute("goingList", goingList)
        model.addAttribute("endedList", endedList)

        return "mystibox/index"
    }

    @GetMapping("/{id}")
    fun detail(
        @PathVariable id: Long,
        model: Model
    ): String {
        model.addAttribute("menu", "mysti");

        val mystibox = mystiboxService.get(id)
        val dto = mystibox.toDto(modelMapper, requestUtil.getContextPath())

        if (dto.status == StatusType.Ended) {
            dto.winnerAddress = mystiboxService.getWinnerAddress(id);
        }

        model.addAttribute("mystibox", dto)
        model.addAttribute("tokenAddress", web3jUtil.tokenAddress)
        model.addAttribute("walletAddress", web3jUtil.walletInAddress)


        return "mystibox/detail"
    }
}