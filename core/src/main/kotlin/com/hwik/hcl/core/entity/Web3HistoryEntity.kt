package com.hwik.hcl.core.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "web3_history")
@EntityListeners(AuditingEntityListener::class)
open class Web3HistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Column(name = "web3_id", nullable = false)
    open var web3Id: Long = -1

    @Column(name = "user_id", nullable = false)
    open var userId: Long = -1

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    open var createdAt: Instant? = null
}