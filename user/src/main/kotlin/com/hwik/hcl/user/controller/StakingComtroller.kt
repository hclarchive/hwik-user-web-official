package com.hwik.hcl.user.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/staking")
class StakingComtroller {
    @GetMapping
    fun index(model : Model): String {
        model.addAttribute("menu", "staking");

        return "staking"
    }
}