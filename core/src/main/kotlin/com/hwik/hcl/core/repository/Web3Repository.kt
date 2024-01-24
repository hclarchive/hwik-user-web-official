package com.hwik.hcl.core.repository

import com.hwik.hcl.core.entity.Web3Entity
import com.hwik.hcl.core.entity.Web3WithProvideDto
import com.hwik.hcl.core.entity.enums.Web3Uuid
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface Web3Repository : JpaRepository<Web3Entity, Long> {
    fun findByUuid(uuid: Web3Uuid): Optional<Web3Entity>
    fun findAllByParentIsNull(): List<Web3Entity>

    @Query(
        value = " SELECT web3.*, COUNT(tx.id) AS participants ,SUM(tx.amount) AS provide, (web3.total_reward - SUM(tx.amount)) AS Remain" +
                " from web3s web3" +
                " left JOIN transaction tx" +
                " ON web3.id = tx.web3_id" +
                " WHERE web3.parent_id = :id" +
                " GROUP BY web3.id"
        , nativeQuery = true
    )
    fun findAllByParentIdWithProvide(id: Long): List<Web3WithProvideDto>
}