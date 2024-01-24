package com.hwik.hcl.core.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener::class)
open class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Column(name = "address", nullable = false, length = 512)
    open var address: String = ""

    @Column(name = "admin_flag", nullable = false)
    open var adminFlag: Boolean = false

    @LastModifiedDate
    @Column(name = "updated_at")
    open var updatedAt: Instant? = null

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    open var createdAt: Instant? = null

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    open var loginHistory: MutableList<LoginHistoryEntity>? = null

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    open var userSns: MutableList<UserSnsEntity>? = null
}