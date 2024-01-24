package com.hwik.hcl.core.repository

import com.hwik.hcl.core.entity.TransactionEntity
import com.hwik.hcl.core.entity.UserEntity
import com.hwik.hcl.core.entity.Web3Entity
import com.hwik.hcl.core.entity.enums.StatusType
import com.hwik.hcl.core.entity.enums.TransactionCategory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.Instant

@Repository
interface TransactionRepository : JpaRepository<TransactionEntity, Long>, JpaSpecificationExecutor<TransactionEntity> {

    fun findByQuizIdAndUserIdAndStatus(quizId: Long, userId: Long, status: StatusType): TransactionEntity?

    fun findByWeb3IdAndUserIdAndStatus(web3Id: Long, userId: Long, status: StatusType): TransactionEntity?

    fun findByWeb3AndStatus(web3: Web3Entity, status: StatusType): List<TransactionEntity>

    fun findByWeb3AndUserAndStatus(web3: Web3Entity, user: UserEntity, status: StatusType): List<TransactionEntity>

    fun findByUserId(id: Long): List<TransactionEntity>

    fun findByUserIdAndCategoryIn(id: Long, categoryList: List<TransactionCategory>, pr: PageRequest): Page<TransactionEntity>

    @Query(value = " SELECT t " +
            " FROM TransactionEntity t " +
            " JOIN FETCH t.mystiboxParticipant mp " +
            " WHERE mp.mystibox.id = :mystiboxId",
        countQuery = " SELECT count(t) " +
                " FROM TransactionEntity t " +
                " JOIN t.mystiboxParticipant mp " +
                " WHERE mp.mystibox.id = :mystiboxId"
        )
    fun findByMystiboxParticipantId(mystiboxId: Long, pageable: Pageable): Page<TransactionEntity>

    @Query(value = """ 
            SELECT t
            FROM TransactionEntity t
            JOIN t.mystiboxParticipant mp ON t.mystiboxParticipant.id = mp.id AND mp.mystibox.id = :mystiboxId AND mp.user.id = :userId
            WHERE t.category in :categoryList
            ORDER BY t.id DESC""")
    fun findByMystiboxId(mystiboxId: Long, userId: Long, categoryList: List<TransactionCategory>): List<TransactionEntity>

    @Query(
        value =
        """
                SELECT SUM(t.fee)
                FROM TransactionEntity t
                WHERE t.createdAt >= :date AND t.category in :categoryList
            """
    )
    fun sumFeeByDateAfterAndCategory(date: Instant, categoryList: List<TransactionCategory>): BigDecimal?


}