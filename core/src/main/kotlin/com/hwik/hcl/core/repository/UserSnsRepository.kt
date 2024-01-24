package com.hwik.hcl.core.repository

import com.hwik.hcl.core.entity.UserSnsEntity
import com.hwik.hcl.core.entity.enums.SnsType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserSnsRepository : JpaRepository<UserSnsEntity, Long>{
    fun findByUserIdAndSnsType(userId: Long, snsType: SnsType): Optional<UserSnsEntity>

    @Query("SELECT us FROM UserSnsEntity us JOIN FETCH us.user WHERE us.snsId = :snsId AND us.snsType = :snsType")
    fun findBySnsIdAndSnsTypeWithUser(snsId: String, snsType: SnsType): Optional<UserSnsEntity>
}