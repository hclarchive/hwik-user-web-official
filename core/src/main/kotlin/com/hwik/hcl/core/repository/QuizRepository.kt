package com.hwik.hcl.core.repository

import com.hwik.hcl.core.entity.QuizEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface QuizRepository : JpaRepository<QuizEntity, Long> {

    @Query(
        value = "SELECT q" +
                " FROM QuizEntity q " +
                " LEFT JOIN FETCH q.image" +
                " WHERE q.deleted = false AND q.enabled = true",
        countQuery = "SELECT count(q.id) " +
                " FROM QuizEntity q " +
                " WHERE q.deleted = false"
    )
    fun findAllByDeletedFalse(pageable: Pageable): Page<QuizEntity>


    @Query(
        "SELECT quiz " +
                "FROM QuizEntity quiz " +
                "JOIN FETCH quiz.questions q " +
                "WHERE quiz.deleted = false AND q.deleted = false AND quiz.id = :id " +
                "ORDER BY q.seq ASC"
    )
    fun findByIdNotDeleted(id: Long): Optional<QuizEntity>

    @Query(
        "SELECT COUNT(q) " +
                "FROM QuizEntity q " +
                "WHERE q.deleted = false"
    )
    fun countAllByNotDeleted(): Long
}