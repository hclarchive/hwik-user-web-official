package com.hwik.hcl.core.entity

import com.hwik.hcl.core.entity.enums.*
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "web3")
@EntityListeners(AuditingEntityListener::class)
@SqlResultSetMapping(
    name = Web3WithProvideDto.SqlResultSetMappingName,
    classes = [
        ConstructorResult(
            targetClass = Web3WithProvideDto::class,
            columns = [
                ColumnResult(name = "id", type = Long::class),
                ColumnResult(name = "parent_id", type = Long::class),
                ColumnResult(name = "title", type = String::class),
                ColumnResult(name = "image_id", type = Long::class),
                ColumnResult(name = "total_reward", type = BigDecimal::class),
                ColumnResult(name = "reward", type = BigDecimal::class),
                ColumnResult(name = "memo", type = String::class),
                ColumnResult(name = "uuid", type = String::class),
                ColumnResult(name = "sns_type", type = String::class),
                ColumnResult(name = "extra_data1", type = String::class),
                ColumnResult(name = "extra_data2", type = String::class),
                ColumnResult(name = "participants", type = Long::class),
                ColumnResult(name = "provide", type = BigDecimal::class),
                ColumnResult(name = "remain", type = BigDecimal::class),
            ]
        )
    ]
)

open class Web3Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    open var children: MutableList<Web3Entity>? = mutableListOf()

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    open var parent: Web3Entity? = null

    @Column(name = "title", nullable = false, length = 256)
    open var title: String? = null

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinColumn(name = "image_id")
    open var image: ImageEntity? = null

    @Column(name = "total_reward", nullable = false)
    open var totalReward: BigDecimal? = null

    @Column(name = "reward", nullable = false, precision = 30, scale = 2)
    open var reward: BigDecimal = 0L.toBigDecimal()

    @Column(name = "memo", nullable = false, length = 512)
    open var memo: String? = null

    @Convert(converter = Web3UuidConverter::class)
    @Column(name = "uuid", nullable = false, length = 512)
    open var uuid: Web3Uuid? = null

    @Column(name = "extra_data1", nullable = true, length = 512)
    open var extraData1: String? = null

    @Column(name = "extra_data2", nullable = true, length = 512)
    open var extraData2: String? = null

    @Convert(converter = SnsTypeConverter::class)
    @Column(name = "sns_type", nullable = false, length = 16)
    open var snsType: SnsType = SnsType.None

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    open var createdAt: Instant? = null

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    open var updatedAt: Instant? = null

    @Transient
    open var participants: Long? = null

    @Transient
    open var provide: BigDecimal? = null

    @Transient
    open var remain: BigDecimal? = null

    @Transient
    open var isComplete: Boolean = false

    @Transient
    open var isClaim: Boolean = false
}