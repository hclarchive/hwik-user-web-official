package com.hwik.hcl.user.util.vo

import com.fasterxml.jackson.annotation.JsonProperty

data class TwitterUserFollowingVo (
    @JsonProperty("following")
    var following: Boolean? = null,
    @JsonProperty("pending_follow")
    var pendingFollow: Boolean? = null,
)
