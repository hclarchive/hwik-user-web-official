package com.hwik.hcl.user.controller.api.v1.dto

data class QuizMetaDto(
    var id: Long? = null,
    var currentIdx: Int? = null,
    var count: Int? = null,
    var content: String? = null,
    var choice1: String? = null,
    var choice2: String? = null,
    var choice3: String? = null,
    var choice4: String? = null,
    var answer: String? = null
)
