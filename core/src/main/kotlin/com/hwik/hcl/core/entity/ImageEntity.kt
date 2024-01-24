package com.hwik.hcl.core.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "image")
@EntityListeners(AuditingEntityListener::class)
open class ImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Column(name = "file_name", length = 1024)
    open var fileName: String? = null

    @Column(name = "original_name", length = 1024)
    open var originalName: String? = null

    @Column(name = "mime_type", length = 256)
    open var mimeType: String? = null

    @Column(name = "deleted", nullable = false)
    open var deleted: Boolean? = false

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    open var createdAt: Instant? = null

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    open var updatedAt: Instant? = null
}