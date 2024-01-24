package com.hwik.hcl.core.repository

import com.hwik.hcl.core.entity.TelegramChatMemberEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface TelegramChatMemberRepository : JpaRepository<TelegramChatMemberEntity, Long> {
    fun findFirstByOrderByIdDesc(): Optional<TelegramChatMemberEntity>
    fun findByUpdateId(updateId: Long): Optional<TelegramChatMemberEntity>

    @Query(value = """
        SELECT COUNT(join_member_id) - COUNT(left_member_id) AS count
        FROM telegram_chat_member 
        WHERE (join_member_id = :memberId OR left_member_id = :memberId) AND chat_id = :chatId
        """
        , nativeQuery = true)
    fun getJoinCountByMemberIdAAndChatId(memberId: String, chatId: Long): Long
    
}