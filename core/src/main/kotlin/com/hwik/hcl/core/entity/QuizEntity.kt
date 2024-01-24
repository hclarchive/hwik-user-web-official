package com.hwik.hcl.core.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "quiz")
@EntityListeners(AuditingEntityListener::class)
open class QuizEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id : Long? = null

    @Column(name = "title", nullable = false, length = 256)
    open var title: String? = null

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinColumn(name = "image_id")
    open var image: ImageEntity? = null

    @Column(name = "reward", nullable = false, precision = 30, scale = 18)
    open var reward: BigDecimal? = null

    @Column(name = "display_order", nullable = false)
    open var displayOrder: Int? = null

    @Column(name = "enabled", nullable = true)
    open var enabled: Boolean = true

    @Column(name = "deleted", nullable = true)
    open var deleted: Boolean = false

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    open var updatedAt: Instant? = null

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    open var createdAt: Instant? = null

    @OneToMany(mappedBy = "quiz", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    open var questions: MutableList<QuestionEntity> = mutableListOf()
}