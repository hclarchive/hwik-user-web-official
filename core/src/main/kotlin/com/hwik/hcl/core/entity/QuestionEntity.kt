package com.hwik.hcl.core.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant


@Entity
@Table(name = "question")
@EntityListeners(AuditingEntityListener::class)
open class QuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    open var quiz: QuizEntity? = null

    @Column(name = "content", nullable = false, length = 1024)
    open var content: String? = null

    @Column(name = "choice_cnt", nullable = false)
    open var choiceCnt: Int? = null

    @Column(name = "answer", nullable = false, length = 1024)
    open var answer: String? = null

    @Column(name = "choice1", nullable = true, length = 1024)
    open var choice1: String? = null

    @Column(name = "choice2", nullable = true, length = 1024)
    open var choice2: String? = null

    @Column(name = "choice3", nullable = true, length = 1024)
    open var choice3: String? = null

    @Column(name = "choice4", nullable = true, length = 1024)
    open var choice4: String? = null

    @Column(name = "choice5", nullable = true, length = 1024)
    open var choice5: String? = null

    @Column(name = "choice6", nullable = true, length = 1024)
    open var choice6: String? = null

    @Column(name = "choice7", nullable = true, length = 1024)
    open var choice7: String? = null

    @Column(name = "choice8", nullable = true, length = 1024)
    open var choice8: String? = null

    @Column(name = "choice9", nullable = true, length = 1024)
    open var choice9: String? = null

    @Column(name = "choice10", nullable = true, length = 1024)
    open var choice10: String? = null

    @Column(name = "seq", nullable = false)
    open var seq: Int? = null

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    open var createdAt: Instant? = null

    @LastModifiedDate
    @Column(name = "update_at", nullable = false)
    open var updateAt: Instant? = null

    @Column(name = "deleted", nullable = false)
    open var deleted: Boolean? = null

    @Transient
    open var userAnswer: AnswerEntity? = null
}