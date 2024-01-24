package com.hwik.hcl.core.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "answer")
@EntityListeners(AuditingEntityListener::class)
open class AnswerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Int? = null

    @ManyToOne
    @JoinColumn(name = "question_id")
    open var question: QuestionEntity? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    open var user: UserEntity? = null

    @Column(name = "answer", length = 1024)
    open var answer: String? = null

    @Column(name = "is_correct")
    open var isCorrect: Boolean? = null

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    open var createdAt: Instant? = null
}