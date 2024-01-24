package com.hwik.hcl.user.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.hwik.hcl.core.util.AesUtil
import com.hwik.hcl.user.util.vo.TelegramUserInfoVo
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URLEncoder
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.reflect.full.memberProperties


@Service
class TelegramUtil(
    private val aesUtil: AesUtil
) : SnsUtilBase(aesUtil) {

    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(this::class.java)
    }

    @Value("\${app.sns.telegram.bot-id}")
    lateinit var botId: String

    @Value("\${app.sns.telegram.bot-token}")
    lateinit var botToken: String

    fun getLoginUrl(domain: String, callbackUrl: String): String {
        val callback = URLEncoder.encode(callbackUrl, "UTF-8")
        val origin = URLEncoder.encode(domain, "UTF-8")
        val url = "https://oauth.telegram.org/auth/auth?bot_id=${botId}&origin=${origin}&embed=1&request_access=write&return_to=${callback}"

        return url
    }

    fun getUserInfo(authResult: String): TelegramUserInfoVo? {
        try {
            logger.info("authResult: ${authResult}")

            val mapper = ObjectMapper()
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            val userInfo = mapper.readValue<TelegramUserInfoVo>(authResult, TelegramUserInfoVo::class.java)

            val map = objectToMap(userInfo)
            map.remove("hash")

            val str: String = map.entries
                .filter { it.value != null }
                .sortedWith(compareBy { it.key.lowercase() })
                .joinToString("\n") {
                    "${it.key}=${it.value}"
                }

            val sk = SecretKeySpec( // Get SHA 256 from telegram token
                MessageDigest.getInstance("SHA-256").digest(
                    botToken.toByteArray()
                ), "HmacSHA256"
            )
            val mac = Mac.getInstance("HmacSHA256")
            mac.init(sk)

            val hashBytes = mac.doFinal(str.toByteArray())

            val sb = StringBuilder()
            for (b in hashBytes) {
                sb.append(String.format("%02x", b))
            }

            val telegramHash = userInfo.hash
            if (sb.toString() == telegramHash) {
                return userInfo
            }

            return null
        } catch (e: Exception) {
            logger.warn(e.toString())
            logger.warn("get UserInfo Fail")
        }

        return null
    }

    fun objectToMap(obj: Any): MutableMap<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        obj::class.memberProperties.forEach { prop ->
            map[prop.name] = prop.call(obj)
        }
        return map
    }
}