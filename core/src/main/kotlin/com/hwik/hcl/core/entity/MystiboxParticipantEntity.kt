package com.hwik.hcl.core.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "mystibox_participant")
@EntityListeners(AuditingEntityListener::class)
open class MystiboxParticipantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mystibox_id", nullable = false)
    open var mystibox: MystiboxEntity? = null

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    open var user: UserEntity? = null

    @Column(name = "approve_hash", nullable = false)
    open var approveHash: String? = null

    @Column(name = "reward_hash", nullable = true)
    open var rewardHash: String? = null

    @OneToMany(mappedBy = "mystiboxParticipant", fetch = FetchType.LAZY)
    open var transactionList: MutableList<TransactionEntity>? = null

    @Column(name = "winner", nullable = true)
    open var winner: Boolean? = null

    @LastModifiedDate
    @Column(name = "updatedAt", nullable = false)
    open var updatedAt: Instant? = null

    @CreatedDate
    @Column(name = "createdAt", nullable = false, updatable = false)
    open var createdAt: Instant? = null


}