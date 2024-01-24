package com.hwik.hcl.user.util.vo

import com.fasterxml.jackson.annotation.JsonProperty

data class TwitterUserInfoVo(
    @JsonProperty("id_str")
    var idStr: String? = null,
    @JsonProperty("name")
    var name: String? = null,
    @JsonProperty("screen_name")
    var screenName: String? = null,
    @JsonProperty("location")
    var location: String? = null,
    @JsonProperty("description")
    var description: String? = null,
    @JsonProperty("url")
    var url: String? = null,
    @JsonProperty("protected")
    var protected: Boolean? = null,
    @JsonProperty("created_at")
    var createdAt: String? = null,
    @JsonProperty("utc_offset")
    var utcOffset: String? = null,
    @JsonProperty("time_zone")
    var timeZone: String? = null,
    @JsonProperty("verified")
    var verified: Boolean? = null,
    @JsonProperty("lang")
    var lang: String? = null,
)