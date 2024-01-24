package com.hwik.hcl.user.service

import com.hwik.hcl.core.entity.LoginHistoryEntity
import com.hwik.hcl.core.entity.UserEntity
import com.hwik.hcl.core.exception.HclException
import com.hwik.hcl.core.ext.toUtf8HexString
import com.hwik.hcl.core.repository.LoginHistoryRepository
import com.hwik.hcl.core.repository.UserRepository
import com.hwik.hcl.core.util.Web3jUtil
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val loginHistoryRepository: LoginHistoryRepository,
    private val web3jUtil: Web3jUtil
) {
    @Transactional
    fun createUuid(address: String, ip: String): String {
        val user = userRepository.findByAddress(address).orElseGet {
            val user = UserEntity()
            user.address = address

            userRepository.save(user)
        }

        val uuid = UUID.randomUUID()
        val uuidAsString = uuid.toString()

        loginHistoryRepository.save(LoginHistoryEntity().apply {
            this.uuid = uuidAsString
            this.user = user
            this.ip = ip
            this.result = false
        })

        return uuidAsString
    }

    fun getUserByAddress(address: String): UserEntity {
        return userRepository.findByAddress(address).orElseThrow()
    }

    @Transactional
    fun checkUuid(address: String, hash: String): Boolean {
        val user = userRepository.findByAddress(address).orElseThrow()

        val loginHistory = loginHistoryRepository.findTopByUserIdOrderByCreatedAtDesc(user.id!!).orElseThrow()
        if (loginHistory.result) {
            throw HclException("already used uuid");
        }

        val uuid = loginHistory.uuid!!
        val msg = "0x${uuid.toUtf8HexString()}"
        val tmpAddress = web3jUtil.recoverAddressFromSignature(msg, hash, address)

        val res = address.equals(tmpAddress, ignoreCase = true)

        return res
    }

    fun updateLoginHistory(address: String, ip: String): Boolean {
        val user = userRepository.findByAddress(address).orElseThrow()
        val loginHistory = loginHistoryRepository.findTopByUserIdOrderByCreatedAtDesc(user.id!!).orElseThrow()

        if (loginHistory.ip == ip) {
            loginHistory.result = true

            loginHistoryRepository.save(loginHistory)

            return true
        }

        return false
    }
}