package com.hwik.hcl.core.repository

import com.hwik.hcl.core.entity.QuestionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface QuestionRepository: JpaRepository<QuestionEntity, Long>{
    @Query(
        "SELECT q, a " +
                "FROM QuestionEntity q " +
                "LEFT JOIN FETCH AnswerEntity a ON q.id = a.question.id AND a.user.id = :userId " +
                "WHERE q.quiz.id = :quizId AND q.deleted = false"
    )
    fun findByQuizIdWithAnswer(quizId: Long?, userId: Long?): List<QuestionEntity>

    fun findByQuizIdAndDeletedFalseOrderBySeq(quizId: Long): List<QuestionEntity>

    fun countByQuizIdAndDeletedFalse(quizId: Long): Int
}