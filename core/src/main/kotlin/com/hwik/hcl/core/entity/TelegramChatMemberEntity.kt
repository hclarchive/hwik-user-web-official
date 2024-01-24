package com.hwik.hcl.core.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "telegram_chat_member")
open class TelegramChatMemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Column(name = "update_id")
    open var updateId: Long = 0

    @Column(name = "join_member_id")
    open var joinMemberId: Long? = null

    @Column(name = "left_member_id")
    open var leftMemberId: Long? = null

    @Column(name = "chat_id")
    open var chatId: Long? = null

    @Column(name = "created_at", nullable = false, updatable = false,)
    open var createdAt: Instant? = null
}