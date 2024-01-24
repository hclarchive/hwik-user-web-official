package com.hwik.hcl.core.repository

import com.hwik.hcl.core.entity.AnswerEntity
import com.hwik.hcl.core.entity.QuestionEntity
import com.hwik.hcl.core.util.mapper.ColumnRowMapper
import org.springframework.data.jpa.repository.Query
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import javax.sql.DataSource

@Repository
class QuestionRepositoryEx(
    private val dataSource: DataSource,
) {

    fun findByQuizIdWithAnswer(quizId: Long?, userId: Long?): List<QuestionEntity> {
        val jdbcTemplate = NamedParameterJdbcTemplate(dataSource);
        val sql =
            """
                SELECT q.*, answer.*
                FROM question q
                LEFT JOIN (
                	SELECT *
                	FROM answer
                	WHERE answer.user_id = :userId
                ) as answer ON answer.question_id = q.id
                WHERE q.quiz_id = :quizId AND q.deleted = false
            """.trimIndent()

        val parameters = MapSqlParameterSource()
        parameters.addValue("quizId", quizId)
        parameters.addValue("userId", userId)

        val mapper = ColumnRowMapper.getMapper(QuestionEntity::class, AnswerEntity::class, QuestionEntity::class, { question, answer ->
            if (answer.id != null) {
                question.userAnswer = answer
            }

            question
        })

        return jdbcTemplate.query<QuestionEntity>(sql, parameters, mapper)
    }
}