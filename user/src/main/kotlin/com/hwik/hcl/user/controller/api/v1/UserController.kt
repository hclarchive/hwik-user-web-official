package com.hwik.hcl.user.controller.api.v1

import com.hwik.hcl.user.service.UserService
import com.hwik.hcl.user.util.RequestUtil
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController("apiV1UserController")
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService,
    private val clientIPService: RequestUtil
) {
    @GetMapping("/{address}/uuid")
    fun getUuid(@PathVariable address: String): ResponseEntity<MutableMap<String, String>> {
        val uuid = userService.createUuid(address, clientIPService.getClientIP())

        val res = mutableMapOf("uuid" to uuid)

        return ResponseEntity.ok(res)
    }
}