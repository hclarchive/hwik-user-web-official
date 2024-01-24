package com.hwik.hcl.core.repository

import com.hwik.hcl.core.entity.LoginHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface LoginHistoryRepository : JpaRepository<LoginHistoryEntity, Long> {
    fun findByUuid(uuid: String): Optional<LoginHistoryEntity>
    fun findTopByUserIdOrderByCreatedAtDesc(userId: Long): Optional<LoginHistoryEntity>
}