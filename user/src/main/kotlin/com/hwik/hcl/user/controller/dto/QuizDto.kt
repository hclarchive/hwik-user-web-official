package com.hwik.hcl.user.controller.dto

import com.hwik.hcl.core.entity.QuestionEntity
import java.math.BigDecimal
import java.time.Instant

data class QuizDto (
    var id : Long? = null,
    var title: String? = null,
    var imageFile: String? = null,
    var reward: BigDecimal? = null,
    var displayOrder: Int? = null,
    var questionCount: Int = 0,
    var currentQuestion: Int = 0,
    var isComplete: Boolean? = false,
    var updatedAt: Instant? = null,
    var createdAt: Instant? = null,
)
