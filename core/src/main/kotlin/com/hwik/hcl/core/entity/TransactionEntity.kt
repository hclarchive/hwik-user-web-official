package com.hwik.hcl.core.entity

import com.hwik.hcl.core.entity.enums.*
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "transaction")
@EntityListeners(AuditingEntityListener::class)
open class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Int? = null

    @Column(name = "category", nullable = false)
    @Convert(converter = TransactionCategoryConverter::class)
    open var category: TransactionCategory? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    open var user: UserEntity? = null

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = true)
    open var quiz: QuizEntity? = null

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "web3_id", nullable = true)
    open var web3: Web3Entity? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mystibox_participant_id", nullable = true)
    open var mystiboxParticipant: MystiboxParticipantEntity? = null

    @Column(name = "title", nullable = false, length = 256)
    open var title: String? = null

    @Column(name = "from_address", nullable = false, length = 256)
    open var fromAddress: String? = null

    @Column(name = "to_address", nullable = false, length = 256)
    open var toAddress: String? = null

    @Column(name = "hash", nullable = true, length = 256)
    open var hash: String? = null

    @Column(name = "explorer", nullable = true, length = 512)
    open var explorer: String? = null

    @Column(name = "amount", nullable = false, precision = 30, scale = 18)
    open var amount: BigDecimal? = null

    @Column(name = "symbol", nullable = false, length = 8)
    open var symbol: String? = null

    @Column(name = "fee", nullable = false, precision = 30, scale = 18)
    open var fee: BigDecimal = BigDecimal.ZERO

    @Convert(converter = StatusTypeConverter::class)
    @Column(name = "status", nullable = false, length = 16)
    open var status: StatusType? = null

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    open var updatedAt: Instant? = null

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    open var createdAt: Instant? = null
}