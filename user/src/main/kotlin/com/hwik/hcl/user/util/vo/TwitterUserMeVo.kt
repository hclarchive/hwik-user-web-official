package com.hwik.hcl.user.util.vo

import com.fasterxml.jackson.annotation.JsonProperty

data class TwitterUserMeVo (
    @JsonProperty("id")
    var id: String? = null,
    @JsonProperty("name")
    var name: String? = null,
    @JsonProperty("username")
    var userName: String? = null,
)
