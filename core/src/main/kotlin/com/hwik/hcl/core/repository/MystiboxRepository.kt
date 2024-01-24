package com.hwik.hcl.core.repository

import com.hwik.hcl.core.entity.MystiboxEntity

import com.hwik.hcl.core.entity.enums.StatusType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.Instant

@Repository
interface MystiboxRepository : JpaRepository<MystiboxEntity, Long> {
    @Query(
        value = "SELECT m" +
                " FROM MystiboxEntity m " +
                " LEFT JOIN FETCH m.image" +
                " WHERE m.deletedAt IS NULL AND m.status in :statusList AND m.enabled = true" +
                " ORDER BY m.id DESC",
        countQuery = "SELECT count(m.id) " +
                " FROM MystiboxEntity m " +
                " WHERE m.deletedAt IS NULL AND m.status in :statusList"
    )
    fun findAllWithImage(statusList: List<StatusType>, pageable: Pageable): Page<MystiboxEntity>

    @Query(
        value = """SELECT m
                FROM MystiboxEntity m
                LEFT JOIN FETCH m.image
                WHERE m.deletedAt IS NULL AND m.status in :statusList
                ORDER BY m.id DESC""",
        countQuery = "SELECT count(m.id) " +
                " FROM MystiboxEntity m " +
                " WHERE m.deletedAt IS NULL AND m.status in :statusList"
    )
    fun findAllWithImageForAdmin(statusList: List<StatusType>, pageable: Pageable): Page<MystiboxEntity>


    @Query(
        value = "UPDATE MystiboxEntity m SET m.curEntryCount = :count WHERE m.id = :id"
    )
    fun updateCurEntryCount(id: Long, count: Int): Int

    @Query(
        value =
        """
            SELECT * 
            FROM mystibox
            WHERE mystibox.id IN (
                SELECT mystibox_id
                FROM mystibox_participant
                WHERE user_id = :userId
                GROUP BY mystibox_id
            ) AND mystibox.enabled = true
            ORDER BY mystibox.ended_at DESC
        """,
        countQuery =
        """SELECT count(mystibox.id) 
            FROM mystibox
            WHERE mystibox.id IN (
                SELECT mystibox_id
                FROM mystibox_participant
                WHERE user_id = :userId
                GROUP BY mystibox_id
            ) AND mystibox.enabled = true""", nativeQuery = true

    )
    fun getParticipantHistory(userId: Long, pageable: Pageable): Page<MystiboxEntity>

    @Query(
        value =
        """
                SELECT SUM(m.burnAmount)
                FROM MystiboxEntity m
                WHERE m.endedAt >= :endDate AND m.deletedAt IS NULL
            """
    )
    fun sumBurnAmountByEndDateAfterAndDeletedAtIsNull(endDate: Instant): BigDecimal?
}