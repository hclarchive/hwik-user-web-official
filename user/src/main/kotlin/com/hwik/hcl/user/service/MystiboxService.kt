package com.hwik.hcl.user.service

import com.hwik.hcl.core.entity.*
import com.hwik.hcl.core.entity.enums.StatusType
import com.hwik.hcl.core.entity.enums.TransactionCategory
import com.hwik.hcl.core.exception.HclException
import com.hwik.hcl.core.repository.*
import com.hwik.hcl.core.util.Web3jUtil
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.*

@Service
class MystiboxService(
    private val mystiboxRepository: MystiboxRepository,
    private val mystiboxParticipantRepository: MystiboxParticipantRepository,
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val web3jUtil: Web3jUtil
) {
    companion object {
        val logger = LoggerFactory.getLogger(MystiboxService::class.java)
    }

    fun get(id: Long): MystiboxEntity {
        val mystibox = mystiboxRepository.findById(id).orElseThrow { HclException("Not found Mystibox") }
        if (mystibox.enabled == false) {
            throw HclException("Not found Mystibox")
        }
        return mystibox
    }

    fun getList(
        start: Int,
        size: Int,
        statusList: List<StatusType> = StatusType.toListAll()
    ): Page<MystiboxEntity> {
        val page = start / size

        return mystiboxRepository.findAllWithImage(
            statusList,
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))
        )
    }

    fun checkEntry(userId: Long, mystiBoxId: Long): Boolean {
        val user = userRepository.findById(userId).orElseThrow { HclException("Not found User") }
        val mystibox = mystiboxRepository.findById(mystiBoxId).orElseThrow { HclException("Not found Mystibox") }

        val applyCount = mystiboxParticipantRepository.findByMystiboxWithUser(mystibox).count { it.user!!.id == userId }
        return applyCount < mystibox.singleEntryLimit!!
    }

    fun entry(userId: Long, mystiBoxId: Long, hash: String): TransactionEntity? {
        val user = userRepository.findById(userId).orElseThrow { HclException("Not found User") }
        val mystibox = mystiboxRepository.findById(mystiBoxId).orElseThrow { HclException("Not found Mystibox") }
        if (mystibox.enabled == false) {
            throw HclException("Not found Mystibox")
        }

        if (checkEntry(userId, mystiBoxId) == false) {
            throw HclException("Already applied")
        }

        for (i in 0..10) {
            if (web3jUtil.checkTx(hash, mystibox.entryRequired) == false) {
                logger.warn("checkTx fail: $hash")
                Thread.sleep(1000)
            } else {
                logger.warn("checkTx success: $hash")
                break
            }
        }

        if (web3jUtil.checkTx(hash, mystibox.entryRequired) == false) {
            throw HclException("Invalid Tx")
        }

        if (mystibox.startedAt!! > Instant.now()) {
            throw HclException("Mystibox is not started")
        } else if (mystibox.endedAt!! < Instant.now()) {
            throw HclException("Mystibox is ended")
        }

        val fromAddress = user.address
        val toAddress = web3jUtil.walletInAddress
        val amount = mystibox.entryRequired

        val receipt = web3jUtil.transferFromErc20Token(fromAddress, toAddress, amount)

        if ("0x1" == receipt.status) {
            val participant = MystiboxParticipantEntity().apply {
                this.mystibox = mystibox
                this.user = user
                this.approveHash = hash
            }
            mystiboxParticipantRepository.save(participant)

            val tx = TransactionEntity().apply {
                this.category = TransactionCategory.MystiBoxApply
                this.mystiboxParticipant = participant
                this.user = user
                this.title = mystibox.title
                this.fromAddress = fromAddress
                this.toAddress = toAddress
                this.hash = receipt.transactionHash
                this.explorer = web3jUtil.getExplorerUrl(receipt.transactionHash)
                this.amount = mystibox.entryRequired
                this.symbol = web3jUtil.symbol
                this.fee = web3jUtil.getFee(receipt)
                this.status = if (receipt.status == "0x1") StatusType.Success else StatusType.Fail
            }

            transactionRepository.save(tx)

            updateEntryCount(mystibox.id!!)

            return tx
        }

        return null
    }

    private fun updateEntryCount(mystiBoxId: Long) {
        val mystibox = mystiboxRepository.findById(mystiBoxId).get()
        mystibox.curEntryCount = mystiboxParticipantRepository.countByMystibox(mystibox)
        mystiboxRepository.save(mystibox)
    }

    fun getParticipantHistory(
        user: UserEntity,
        page: Int,
        size: Int
    ): Page<MystiboxEntity> {
        val mystiboxList: Page<MystiboxEntity> =
            mystiboxRepository.getParticipantHistory(user.id!!, PageRequest.of(page, size))

        if (mystiboxList.hasContent()) {
            mystiboxList.forEach {
                it.transactionList = transactionRepository.findByMystiboxId(it.id!!, user.id!!, listOf(TransactionCategory.MystiBoxApply, TransactionCategory.MystiBoxReward))
                it.isWinner = (getWinnerAddress(it.id!!) == user.address)
            }
        }

        return mystiboxList
    }

    fun getWinnerAddress(mystiboxId: Long): String? {
        return mystiboxParticipantRepository.findWinner(mystiboxId)?.let {
            return it.address
        }
    }
}