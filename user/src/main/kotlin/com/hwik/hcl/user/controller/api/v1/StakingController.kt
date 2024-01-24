package com.hwik.hcl.user.controller.api.v1

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@RestController("apiStakingController")
@RequestMapping("api/v1/staking")
class StakingController {

    @GetMapping("/pool")
    fun getPoolList() {
        println("getList pool")
    }

    @GetMapping("/my")
    fun getMyList() {
        println("getList my")
    }

    @PostMapping("/{id}/claim")
    fun claim(@PathVariable id: String) {
        println("calim")
    }

    @PostMapping("/{id}/staking")
    fun staking(@PathVariable id: String) {
        println("stakin")
    }

    @PostMapping("/{id}/nft")
    fun nft(@PathVariable id: String,
            ) : ResponseEntity<Void> {
        println("nft")

        return ResponseEntity.ok().build();
    }

}