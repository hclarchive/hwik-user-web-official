package com.hwik.hcl.user.controller

import com.hwik.hcl.user.service.QuizService
import com.hwik.hcl.user.service.Web3Service
import com.hwik.hcl.user.controller.dto.QuizDto
import com.hwik.hcl.user.controller.dto.Web3Dto
import com.hwik.hcl.user.util.UserUtil
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/quest")
class QuestController(
    private val modelMapper: ModelMapper,
    private val quizService: QuizService,
    private val web3Service: Web3Service,
    private val userUtil: UserUtil
) {
    @GetMapping
    fun index(model: Model): String {
        model.addAttribute("menu", "quest");

        val user = userUtil.getUser()

        val web3List = web3Service.getWeb3List().map {
            val dto = modelMapper.map(it, Web3Dto::class.java)

            dto.web3Count = it.children?.size ?: 0

            if (user != null) {
                dto.isComplete = web3Service.isClaim(it, user)
                dto.currentWeb3 = it.children?.filter { web3Service.isClaim(it, user) }?.size ?: 0
            }

            dto
        }.toList()
        model.addAttribute("web3", web3List)

        val quizList = quizService.getQuizList(false, "displayOrder").map {
            val dto = modelMapper.map(it, QuizDto::class.java)
            val quizId = it.id!!

            dto.questionCount = quizService.getQuestionCount(quizId)

            if (user != null) {
                dto.isComplete = quizService.isClaim(quizId, user)

                if (dto.isComplete == false) {
                    val questionList = quizService.getQuestionList(quizId)
                    val currentQuestion = quizService.getCurrentQuestion(quizId, user);
                    val index = questionList.withIndex().firstOrNull() { currentQuestion?.id == it.value.id }?.index
                        ?: questionList.size

                    dto.currentQuestion = index
                }
            } else {
                dto.currentQuestion = 0
            }

            dto
        }

        model.addAttribute("quiz", quizList)

        return "quest"
    }
}