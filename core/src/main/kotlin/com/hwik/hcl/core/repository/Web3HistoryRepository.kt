package com.hwik.hcl.core.repository

import com.hwik.hcl.core.entity.Web3HistoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface Web3HistoryRepository : JpaRepository<Web3HistoryEntity, Long> {
    fun findByUserIdAndWeb3Id(userId: Long, web3Id: Long): Web3HistoryEntity?

    @Modifying
    @Query(
        """
            insert into web3_history (user_id, web3_id, created_at) values (:userId, :web3Id, FROM_UNIXTIME(:time))
        """
        , nativeQuery = true
    )
    fun add(userId: Long, web3Id: Long, time: Long)
}