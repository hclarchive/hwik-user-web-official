package com.hwik.hcl.user.controller.api.v1

import com.hwik.hcl.core.entity.UserEntity
import com.hwik.hcl.core.exception.HclException
import com.hwik.hcl.user.service.QuizService
import com.hwik.hcl.user.service.UserService
import com.hwik.hcl.user.service.Web3Service
import com.hwik.hcl.user.controller.api.v1.dto.QuizMetaDto
import com.hwik.hcl.user.controller.api.v1.dto.TransactionDto
import com.hwik.hcl.user.util.UserUtil
import org.modelmapper.ModelMapper
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/api/v1/quiz")
@RequestMapping("/api/v1/quiz")
class QuizController(
    private val quizService: QuizService,
    private val web3Service: Web3Service,
    private val userService: UserService,
    private val modelMapper: ModelMapper,
    private val userUtil: UserUtil
) {

    fun getCurrentQuiz(quizId: Long, user: UserEntity): ResponseEntity<QuizMetaDto> {
        val questionList = quizService.getQuestionList(quizId)
        val currentQuestion = quizService.getCurrentQuestion(quizId, user);
        val index = questionList.withIndex().firstOrNull() { currentQuestion?.id == it.value.id }?.index ?: questionList.size

        return ResponseEntity.ok(
            QuizMetaDto(
                id = currentQuestion?.id ?: -1,
                count = questionList.size,
                currentIdx = index,
                content = currentQuestion?.content ?: "",
                choice1 = currentQuestion?.choice1 ?: "",
                choice2 = currentQuestion?.choice2 ?: "",
                choice3 = currentQuestion?.choice3 ?: "",
                choice4 = currentQuestion?.choice4 ?: "",
            )
        )
    }

    @PostMapping("/{quizId}/claim")
    fun claimQuiz(
        @PathVariable quizId: Long
    ): ResponseEntity<TransactionDto> {
        val user = userUtil.getUser() ?: throw HclException("login required")

        val tx = quizService.claimQuiz(quizId, user)
        val txDto = modelMapper.map(tx, TransactionDto::class.java)

        return ResponseEntity.ok(txDto)
    }

    @PostMapping("/{quizId}/start")
    fun getQuiz(
        @PathVariable quizId: Long
    ): ResponseEntity<QuizMetaDto> {
        val user = userUtil.getUser() ?: throw HclException("login required")

        return getCurrentQuiz(quizId, user)
    }

    @PostMapping("/{quizId}/{questionId}")
    fun answer(
        @PathVariable quizId: Long,
        @PathVariable questionId: Long,
        @RequestBody quizMeta: QuizMetaDto
    ): ResponseEntity<QuizMetaDto> {
        val user = userUtil.getUser() ?: throw HclException("login required")
        val answer = quizMeta.answer ?: throw HclException("invalid answer")

        if (quizService.saveAnswer(quizId, questionId, user, answer)) {
            return getCurrentQuiz(quizId, user)
        }

        return ResponseEntity.badRequest().build()
    }
}