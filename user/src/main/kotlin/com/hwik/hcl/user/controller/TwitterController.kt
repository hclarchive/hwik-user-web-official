package com.hwik.hcl.user.controller

import com.hwik.hcl.user.service.UserService
import com.hwik.hcl.user.service.Web3Service
import com.hwik.hcl.user.util.TwitterUtil
import com.hwik.hcl.user.util.RequestUtil
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/twitter")
class TwitterController(
    private val web3Service: Web3Service,
    private val userService: UserService,
    private val twitterUtil: TwitterUtil,
    private val requestUtil: RequestUtil
) {
    @GetMapping
    fun index() : String
    {
        return "oauth"
    }

    @GetMapping("/oauth")
    fun oauth(
        @RequestParam state: String,
        @RequestParam(required = false, defaultValue = "")  code: String,
        @RequestParam(required = false, defaultValue = "") error: String,
    ): String {
        if (error.isNotEmpty()) {
            return "redirect:${requestUtil.getDomain()}/web3/oauth?msg=${error.replace("_", " ")}";
        }
        else {
            try {
                web3Service.signTwitter(requestUtil.getDomain(), state, code)
                return "redirect:${requestUtil.getDomain()}/web3/oauth?msg=success";
            } catch (e: Exception) {
                return "redirect:${requestUtil.getDomain()}/web3/oauth?msg=${e.message}";
            }
        }

    }
}