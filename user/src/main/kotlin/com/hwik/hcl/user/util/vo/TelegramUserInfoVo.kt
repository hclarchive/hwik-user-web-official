package com.hwik.hcl.user.util.vo

import com.fasterxml.jackson.annotation.JsonProperty

data class TelegramUserInfoVo (
    @JsonProperty("id")
    var id: Long? = null,
    @JsonProperty("first_name")
    var first_name: String? = null,
    @JsonProperty("last_name")
    var last_name: String? = null,
    @JsonProperty("auth_date")
    var auth_date: Long? = null,
    @JsonProperty("photo_url")
    var photoUrl: String? = null,
    @JsonProperty("username")
    var username: String? = null,
    @JsonProperty("hash")
    var hash: String? = null,
)