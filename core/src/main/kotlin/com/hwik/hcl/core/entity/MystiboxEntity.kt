package com.hwik.hcl.core.entity

import com.hwik.hcl.core.entity.enums.StatusType
import com.hwik.hcl.core.entity.enums.StatusTypeConverter
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.Instant
import kotlin.jvm.Transient

@Entity
@Table(name = "mystibox")
@EntityListeners(AuditingEntityListener::class)
open class MystiboxEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Column(name = "title", nullable = false, length = 128)
    open var title: String = ""

    @Column(name = "entry_required", nullable = false, precision = 20, scale = 2)
    open var entryRequired: BigDecimal = 0.0.toBigDecimal()

    @Column(name = "single_entry_limit", nullable = false)
    open var singleEntryLimit: Int? = null

    @Column(name = "cur_entry_count", nullable = false)
    open var curEntryCount: Int? = null

    @Column(name = "max_entry_count", nullable = false)
    open var maxEntryCount: Int? = null

    @Column(name = "reward", nullable = false)
    open var reward: Int? = null

    @Column(name = "coupon", nullable = true, length = 256)
    open var coupon: String? = null

    @Column(name = "reward_wemix", nullable = false, precision = 20, scale = 6)
    open var rewardWemix: BigDecimal = 0.toBigDecimal()

    @Convert(converter = StatusTypeConverter::class)
    @Column(name = "status", nullable = false, length = 16)
    open var status: StatusType? = null

    @Column(name = "burn_rate", nullable = false, precision = 20, scale = 2)
    open var burnRate: BigDecimal = 0.toBigDecimal()

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "image_id")
    open var image: ImageEntity? = null

    @Column(name = "burn_hash", nullable = true)
    open var burnHash: String? = null

    @Column(name = "burn_amount", nullable = false, precision = 20, scale = 2)
    open var burnAmount: BigDecimal = 0.toBigDecimal()


    @Column(name = "enabled", nullable = false)
    open var enabled: Boolean = true

    @Column(name = "started_at", nullable = false)
    open var startedAt: Instant? = null

    @Column(name = "ended_at", nullable = false)
    open var endedAt: Instant? = null

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    open var updatedAt: Instant? = null

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    open var createdAt: Instant? = null

    @Column(name = "deleted_at", nullable = true)
    open var deletedAt: Instant? = null

    @Transient
    open var transactionList: List<TransactionEntity>? = null

    @Transient
    open var isWinner: Boolean = false

}