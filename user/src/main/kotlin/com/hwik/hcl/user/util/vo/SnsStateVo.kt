package com.hwik.hcl.user.util.vo

import java.time.Instant

data class SnsStateVo (
    var address : String = "",
    var web3Id : Long = 0,
    var time : Instant = Instant.now()
)