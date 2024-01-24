package com.hwik.hcl.user.util.vo

import com.fasterxml.jackson.annotation.JsonProperty

data class TwitterResVo<T> (
    @JsonProperty("data")
    var data: T? = null
)