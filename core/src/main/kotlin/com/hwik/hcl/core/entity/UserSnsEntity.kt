package com.hwik.hcl.core.entity

import com.hwik.hcl.core.entity.enums.SnsType
import com.hwik.hcl.core.entity.enums.SnsTypeConverter
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "user_sns")
@EntityListeners(AuditingEntityListener::class)
open class UserSnsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    open var user: UserEntity? = null

    @Convert(converter = SnsTypeConverter::class)
    @Column(name = "sns_type", nullable = false, length = 50)
    open var snsType: SnsType = SnsType.None

    @Column(name = "sns_id", nullable = true, length = 256)
    open var snsId: String? = null

    @Column(name = "sns_name", nullable = true, length = 256)
    open var snsName: String? = null

    @Column(name = "token", nullable = true, length = 256)
    open var token: String? = null

    @Column(name = "refresh_token", nullable = true, length = 256)
    open var refreshToken: String? = null

    @Column(name = "state", nullable = true, length = 256)
    open var state: String? = null

    @LastModifiedDate
    @Column(name = "update_at", nullable = false)
    open var updateAt: Instant? = null

    @CreatedDate
    @Column(name = "create_at", nullable = false, updatable = false)
    open var createAt: Instant? = null
}