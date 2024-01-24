package com.hwik.hcl.user.service

import com.hwik.hcl.core.entity.*
import com.hwik.hcl.core.entity.enums.StatusType
import com.hwik.hcl.core.entity.enums.TransactionCategory
import com.hwik.hcl.core.exception.HclException
import com.hwik.hcl.core.repository.*
import com.hwik.hcl.core.util.Web3jUtil
import com.hwik.hcl.core.util.isSucceed
import com.hwik.hcl.core.util.toFee
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.math.BigDecimal


@Service
class QuizService(
    private val questionRepository: QuestionRepository,
    private val questionRepositoryEx: QuestionRepositoryEx,
    private val quizRepository: QuizRepository,
    private val transactionRepository: TransactionRepository,
    private val answerRepository: AnswerRepository,
    private val entityManager: EntityManager,
    private val web3jUtil: Web3jUtil
) {

    fun getQuizList(
        isDesc: Boolean? = true,
        columnName: String? = "id"
    ): List<QuizEntity> {
        val quiz = getQuizList(0, 9999, isDesc, columnName);
        return quiz.content
    }

    fun getQuizList(
        start: Int,
        size: Int,
        isDesc: Boolean? = true,
        columnName: String? = "id"
    ): Page<QuizEntity> {
        val direction = if (isDesc == true) Sort.Direction.DESC else Sort.Direction.ASC
        val property = columnName ?: "id"
        val page = start / size
        val quizList = quizRepository.findAllByDeletedFalse(PageRequest.of(page, size, Sort.by(direction, property)))

        return quizList;
    }

    fun getQuestionList(quizId: Long): List<QuestionEntity> {
        return questionRepository.findByQuizIdAndDeletedFalseOrderBySeq(quizId);
    }

    @Transactional
    fun saveAnswer(quizId: Long, questionId: Long, user: UserEntity, answer: String): Boolean {
        val question = questionRepository.findById(questionId).orElseThrow()
        if (question.quiz?.id != quizId) {
            throw HclException("invalid quizId")
        }
        if (question.answer != answer) {
//            throw HclException("invalid answer")
            return false
        }

        val entity = AnswerEntity().apply {
            this.question = question
            this.user = user
            this.answer = answer
            this.isCorrect = true
        }

        answerRepository.save(entity)
        return true
    }

    fun getCurrentQuestion(quizId: Long, user: UserEntity): QuestionEntity? {
        entityManager.clear()

        val questions = questionRepositoryEx.findByQuizIdWithAnswer(quizId, user.id!!)

        return questions.filter { it -> it.userAnswer == null }.firstOrNull()
    }

    fun claimQuiz(quizId: Long, user: UserEntity): TransactionEntity {
        val quiz = quizRepository.findById(quizId).orElseThrow { HclException("invalid quizId") }
        val reward = quiz.reward ?: throw HclException("invalid reward")
        val currentQuestion = getCurrentQuestion(quizId, user)

        if (currentQuestion == null) {
            if (transactionRepository.findByQuizIdAndUserIdAndStatus(quizId, user.id!!, StatusType.Success) != null) {
                throw HclException("already finished")
            }

            if (transactionRepository.findByQuizIdAndUserIdAndStatus(quizId, user.id!!, StatusType.Ing) != null) {
                throw HclException("already in progress")
            }

            val tx = TransactionEntity().apply {
                this.fromAddress = web3jUtil.walletInAddress
                this.toAddress = user.address
                this.category = TransactionCategory.Quiz
                this.title = quiz.title
                this.user = user
                this.quiz = quiz
                this.symbol = "HWIK"
                this.fee = BigDecimal.ZERO
                this.amount = reward
                this.status = StatusType.Ing
            }

            transactionRepository.save(tx)

            val receipt: TransactionReceipt =
                web3jUtil.transferErc20Token(user.address, reward) ?: throw HclException("transfer fail");

            val status: StatusType = if (receipt.isSucceed()) StatusType.Success else StatusType.Fail

            tx.hash = receipt.transactionHash
            tx.fee = receipt.toFee()
            tx.explorer = web3jUtil.getExplorerUrl(receipt.transactionHash)
            tx.status = status

            transactionRepository.save(tx)

            if (receipt.isSucceed()) {
                return tx
            } else {
                throw HclException("transfer fail")
            }
        } else {
            throw HclException("not finished")
        }
    }

    fun isClaim(quizId: Long, user: UserEntity): Boolean? {
        val userId = user.id ?: throw HclException("invalid userId")

        if (transactionRepository.findByQuizIdAndUserIdAndStatus(quizId, userId, StatusType.Success) != null) {
            return true
        }
        return false
    }

    fun getQuestionCount(quizId: Long): Int {
        return questionRepository.countByQuizIdAndDeletedFalse(quizId)
    }
}