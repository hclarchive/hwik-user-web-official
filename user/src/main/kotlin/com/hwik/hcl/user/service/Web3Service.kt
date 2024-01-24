package com.hwik.hcl.user.service

import com.hwik.hcl.core.entity.*
import com.hwik.hcl.core.entity.enums.SnsType
import com.hwik.hcl.core.entity.enums.StatusType
import com.hwik.hcl.core.entity.enums.TransactionCategory
import com.hwik.hcl.core.entity.enums.Web3Uuid
import com.hwik.hcl.core.exception.HclException
import com.hwik.hcl.core.repository.*
import com.hwik.hcl.user.util.vo.TelegramUserInfoVo
import com.hwik.hcl.user.util.vo.TwitterAccessTokenVo
import com.hwik.hcl.user.util.vo.SnsStateVo
import com.hwik.hcl.user.util.vo.TwitterUserMeVo
import com.hwik.hcl.core.util.*
import com.hwik.hcl.user.util.TelegramUtil
import com.hwik.hcl.user.util.TwitterUtil
import jakarta.persistence.EntityManager
import jakarta.persistence.Query
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.math.BigDecimal


@Service
class Web3Service(
    private val web3Repository: Web3Repository,
    private val entityManager: EntityManager,
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val userSnsRepository: UserSnsRepository,
    private val web3HistoryRepository: Web3HistoryRepository,
    private val web3jUtil: Web3jUtil,
    private val twitterUtil: TwitterUtil,
    private val telegramUtil: TelegramUtil, private val telegramChatMemberRepository: TelegramChatMemberRepository,
) {

    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(this::class.java)
    }

    fun getWeb3List(): List<Web3Entity> {
        return web3Repository.findAllByParentIsNull()
    }

    fun getWeb3WithProvide(uuid: String): Web3Entity {
        val web3UUid = Web3Uuid.fromString(uuid) ?: throw HclException("invalid uuid")
        val web3 = web3Repository.findByUuid(web3UUid).orElseThrow {
            HclException("Web3 not found")
        }

        val sql =
            """SELECT web3.*, COUNT(tx.id) AS participants ,SUM(tx.amount) AS provide, (web3.total_reward - SUM(tx.amount)) AS Remain 
              from web3 web3
              left JOIN transaction tx 
              ON web3.id = tx.web3_id
              WHERE web3.parent_id = :id 
              GROUP BY web3.id"""
        val query: Query = entityManager.createNativeQuery(sql, "Web3WithProvideDtoMapping")
        query.setParameter("id", web3.id!!)
        val results: List<Web3WithProvideDto> = query.resultList as List<Web3WithProvideDto>

        web3.children = results.map {
            val reward = it.reward ?: throw HclException("invalid reward")
            Web3Entity().apply {
                this.id = it.id
                this.title = it.title
                this.totalReward = it.totalReward
                this.reward = reward
                this.memo = it.memo
                this.extraData1 = it.extraData1
                this.extraData2 = it.extraData2
                this.uuid = Web3Uuid.fromString(it.uuid!!)
                this.snsType = SnsType.fromString(it.snsType ?: "")
                this.participants = it.participants ?: 0
                this.provide = it.provide ?: 0.0.toBigDecimal()
                this.remain = BigDecimal.ZERO.max(it.remain ?: it.totalReward)
                this.image = ImageEntity().apply {
                    id = it.imageId
                }
            }
        }.toMutableList()

        return web3;
    }


    fun checkWeb3Status(web3: Web3Entity, user: UserEntity) {
        web3.children?.forEach {
            val uuid = it.uuid ?: throw HclException("Invalid web3 info")
            val tmpWeb3 = web3Repository.findByUuid(uuid).orElseThrow {
                HclException("Web3 not found")
            }


            it.isClaim = isClaim(tmpWeb3, user)

            if (it.isClaim == false) {
                it.isComplete = checkComplete(tmpWeb3, user)
            }
        }
    }


    @Transactional
    fun checkComplete(web3: Web3Entity, user: UserEntity): Boolean {
        val uuid = web3.uuid ?: return false

        when (uuid) {
            Web3Uuid.platform -> {
                return web3.children?.all { it -> checkComplete(it, user) } ?: false
            }

            Web3Uuid.community -> {
                return web3.children?.all { it -> checkComplete(it, user) } ?: false
            }

            Web3Uuid.platform_wallet -> {
                return true
            }

            Web3Uuid.community_twitter -> {
//                web3HistoryRepository.findByUserAndWeb3(user, web3) ?: return false
                web3HistoryRepository.findByUserIdAndWeb3Id(user.id!!, web3.id!!) ?: return false
                return true
            }

            Web3Uuid.community_telegram_gb, Web3Uuid.community_telegram_kr -> {

                if (web3HistoryRepository.findByUserIdAndWeb3Id(user.id!!, web3.id!!) != null) {
                    return true
                }

                val snsUser = userSnsRepository.findByUserIdAndSnsType(user.id!!, SnsType.Telegram).orElse(null)
                if (snsUser == null) {
                    return false
                }

                val snsId = snsUser.snsId ?: return false;
                var chatId = web3.extraData2?.toLong() ?: return false;
                val joincount = telegramChatMemberRepository.getJoinCountByMemberIdAAndChatId(snsId, chatId)

                if (joincount > 0) {
                    return true
                }
            }

            else ->
                return false
        }

        return false
    }

    fun isClaim(it: Web3Entity, user: UserEntity): Boolean {
        val tx = transactionRepository.findByWeb3AndUserAndStatus(it, user, StatusType.Success).firstOrNull();
        return tx != null
    }

    @Transactional
    fun claim(user: UserEntity, web3Id: Long): TransactionEntity {
        val web3 = web3Repository.findById(web3Id).orElseThrow {
            HclException("Web3 not found")
        }

        val isComplete = checkComplete(web3, user)
        if (isComplete == false) {
            throw HclException("Not complete")
        }

        val existTx: TransactionEntity? =
            transactionRepository.findByWeb3AndUserAndStatus(web3, user, StatusType.Success).firstOrNull()
        if (existTx != null) {
            return existTx
        }
        val existTx2: TransactionEntity? =
            transactionRepository.findByWeb3AndUserAndStatus(web3, user, StatusType.Ing).firstOrNull()
        if (existTx2 != null) {
            return throw HclException("Already claim")
        }

        val amountList = transactionRepository.findByWeb3AndStatus(web3, StatusType.Success).map { it.amount }

        var sum: BigDecimal = BigDecimal.ZERO
        for (amount in amountList) {
            sum += (amount ?: BigDecimal.ZERO)
        }

        if (sum >= web3.totalReward) {
            throw HclException("Already max reward")
        }

        val tx = TransactionEntity().apply {
            this.user = user
            this.category = TransactionCategory.Web3
            this.web3 = web3
            this.title = web3.title
            this.fromAddress = web3jUtil.walletInAddress
            this.toAddress = user.address
            this.amount = web3.reward
            this.symbol = "HWIK"
            this.status = StatusType.Ing
        }

        transactionRepository.save(tx)

        val receipt: TransactionReceipt =
            web3jUtil.transferErc20Token(user.address, web3.reward) ?: throw HclException("Failed to claim")

        val status: StatusType = if (receipt.isSucceed()) StatusType.Success else StatusType.Fail
        tx.hash = receipt.transactionHash
        tx.explorer = web3jUtil.getExplorerUrl(receipt.transactionHash)
        tx.fee = receipt.toFee()
        tx.status = status

        transactionRepository.save(tx)

        if (receipt.isSucceed()) {
            return tx
        }

        throw HclException("Failed to claim")
    }

    fun getPopupUrl(user: UserEntity, web3Id: Long, domain: String): String? {
        val web3 = web3Repository.findById(web3Id).orElseThrow {
            HclException("Web3 not found")
        }

        if (web3.snsType == SnsType.None) {
            throw HclException("Invalid access")
        }

        val userId = user.id ?: throw HclException("Invalid user")
        val address = user.address
        val snsType = web3.snsType

        if (snsType == SnsType.Twitter) {
            if (web3HistoryRepository.findByUserIdAndWeb3Id(user.id!!, web3.id!!) != null) {
                return "/web3/oauth?msg=Already followed"
            }

            val state = twitterUtil.encryptState(SnsStateVo(address, web3Id))
            return twitterUtil.getLoginUrl("${domain}/twitter/oauth", state)

        } else if (snsType == SnsType.Telegram) {
            val userSns = userSnsRepository.findByUserIdAndSnsType(userId, snsType).orElse(null)

            if (userSns == null) {
                return "telegram_login:${telegramUtil.botId}"
            } else {
                return "https://t.me/${web3.extraData1}"
            }
        }

        return null
    }

    fun signTwitter(domain: String, state: String, code: String): String {
        logger.info("domain: $domain")

        val twitterState: SnsStateVo = twitterUtil.decryptState(state) ?: throw HclException("Invalid state")
        val user = userRepository.findByAddress(twitterState.address).orElseThrow {
            HclException("User not found")
        }

        val web3 = web3Repository.findById(twitterState.web3Id).orElseThrow {
            HclException("Web3 not found")
        }

        val followId = web3.extraData2 ?: throw HclException("Invalid web3 info")

        val accessTokenVo: TwitterAccessTokenVo = twitterUtil.getAccessToken("${domain}/twitter/oauth", code)
            ?: throw HclException("Failed to get access token")
        val accresToken = accessTokenVo.access_token ?: throw HclException("Failed to get access token")

        val me: TwitterUserMeVo = twitterUtil.getUserId(accresToken) ?: throw HclException("Failed to get user info")
        val snsId = me.id ?: throw HclException("Invalid user")

        val existUserSns = userSnsRepository.findBySnsIdAndSnsTypeWithUser(snsId, SnsType.Twitter).orElse(null)

        if (existUserSns != null) {
            val tmpUser = existUserSns.user ?: throw HclException("Invalid user info")

            if (tmpUser.address != twitterState.address) {
                throw HclException("This Twitter has been connected to another address: ${tmpUser.address}")
            }
        } else {
            userSnsRepository.save(UserSnsEntity().apply {
                this.user = user
                this.snsType = SnsType.Twitter
                this.snsId = snsId
                this.snsName = me.userName
            })
        }

        val follow = twitterUtil.follow(accresToken, snsId, followId)

        if (follow != true) {
            throw HclException("Failed to follow")
        }

        web3HistoryRepository.save(Web3HistoryEntity().apply {
            this.userId = user.id!!
            this.web3Id = web3.id!!
        });

        return user.address;
    }

    fun signTelegram(user: UserEntity, tgAuthResult: String): String? {
        val userInfo: TelegramUserInfoVo =
            telegramUtil.getUserInfo(tgAuthResult) ?: throw HclException("Failed to get user info")

        val snsId = userInfo.id?.toString() ?: throw HclException("Invalid user info")

        val existUserSns = userSnsRepository.findBySnsIdAndSnsTypeWithUser(snsId, SnsType.Telegram).orElse(null)

        if (existUserSns != null) {
            val tmpUser = existUserSns.user ?: throw HclException("Invalid user info")

            if (tmpUser.address != user.address) {
                throw HclException("This Telegram has been connected to another address: ${tmpUser.address}")
            }
        }

        val userSns = UserSnsEntity().apply {
            this.user = user
            this.snsType = SnsType.Telegram
            this.snsId = snsId
            this.snsName = "${userInfo?.first_name} ${userInfo?.last_name}"
        }
        userSnsRepository.save(userSns)

        return user.address;
    }
}
