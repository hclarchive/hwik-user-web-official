package com.hwik.hcl.user.controller

import com.hwik.hcl.core.entity.UserEntity
import com.hwik.hcl.user.service.Web3Service
import com.hwik.hcl.user.controller.dto.toDto
import com.hwik.hcl.user.util.RequestUtil
import com.hwik.hcl.user.util.UserUtil
import org.modelmapper.ModelMapper
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/web3")
class Web3Controller(
    private val modelMapper: ModelMapper,
    private val web3Service: Web3Service,
    private val userUtil: UserUtil,
    private val requestUtil: RequestUtil,
) {

    @GetMapping("/{uuid}")
    fun index(
        @PathVariable uuid: String,
        model: Model
    ): String {

        model.addAttribute("menu", "quest")

        val web3 = web3Service.getWeb3WithProvide(uuid)

        val user = userUtil.getUser()
        if (user != null) {
            web3Service.checkWeb3Status(web3, user)
        }

        val web3Dto = web3.toDto(modelMapper, requestUtil.getContextPath())
        model.addAttribute("web3", web3Dto)

        return "web3/index"
    }

    @GetMapping("/oauth")
    fun oauth(
    ): String {
        return "web3/oauth"
    }

}