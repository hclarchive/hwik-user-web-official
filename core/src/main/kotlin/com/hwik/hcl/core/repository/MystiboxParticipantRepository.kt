package com.hwik.hcl.core.repository

import com.hwik.hcl.core.entity.MystiboxEntity
import com.hwik.hcl.core.entity.MystiboxParticipantEntity
import com.hwik.hcl.core.entity.UserEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MystiboxParticipantRepository : JpaRepository<MystiboxParticipantEntity, Long> {
    fun countByMystibox(mystiBox: MystiboxEntity): Int

    @Query(
        value = " SELECT mp " +
                " FROM MystiboxParticipantEntity mp " +
                " JOIN FETCH mp.user " +
                " WHERE mp.mystibox = :mystibox "
    )
    fun findByMystiboxWithUser(mystibox: MystiboxEntity): List<MystiboxParticipantEntity>

    @Query(
        value = " SELECT count(mp.id)"  +
                " FROM MystiboxParticipantEntity mp " +
                " WHERE mp.mystibox = :mystibox AND mp.user = :user AND mp.winner = true "
    )
    fun isWinner(mystibox: MystiboxEntity, user: UserEntity): Int

    @Query(
        value = " SELECT mp.user"  +
                " FROM MystiboxParticipantEntity mp " +
                " WHERE mp.mystibox.id = :mystiboxId AND mp.winner = true "
    )
    fun findWinner(mystiboxId: Long): UserEntity?


    fun findByMystibox(mystibox: MystiboxEntity, pageRequest: PageRequest): Page<MystiboxParticipantEntity>

    @Query(
        value =
            """
                SELECT COUNT(DISTINCT par.user_id)
                FROM mystibox_participant par
                WHERE par.mystibox_id IN :mystiboxIdList
            """
        , nativeQuery = true
    )
    fun countUserByMystiboxIdlist(mystiboxIdList: List<Long>): Int
}