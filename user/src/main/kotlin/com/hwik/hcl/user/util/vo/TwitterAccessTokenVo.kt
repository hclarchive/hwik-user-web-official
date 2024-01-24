package com.hwik.hcl.user.util.vo

import com.fasterxml.jackson.annotation.JsonProperty

data class TwitterAccessTokenVo (
    @JsonProperty("token_type")
    var token_type: String? = null,
    @JsonProperty("expires_in")
    var expires_in: Long? = null,
    @JsonProperty("access_token")
    var access_token: String? = null,
    @JsonProperty("scope")
    var scope: String? = null,
    @JsonProperty("refresh_token")
    var refresh_token: String? = null,
)