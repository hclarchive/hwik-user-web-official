package com.hwik.hcl.user.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.hwik.hcl.user.util.vo.SnsStateVo
import org.springframework.beans.factory.annotation.Value
import com.hwik.hcl.core.util.AesUtil
import java.time.Instant

abstract class SnsUtilBase (
    private val aesUtil: AesUtil
){
    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(this::class.java)
    }

    @Value("\${app.sns.state-key}")
    lateinit var stateKey: String

    fun encryptState(data: SnsStateVo): String {
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())

        val json = mapper.writeValueAsString(data)
        val encrypted = aesUtil.encrypt(json, stateKey);

        return encrypted
    }

    fun decryptState(state: String): SnsStateVo? {
        try {
            val decrypted = aesUtil.decrypt(state, stateKey)

            val mapper = ObjectMapper()
            mapper.registerModule(JavaTimeModule())
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            val res = mapper.readValue(decrypted, SnsStateVo::class.java)

            if (res.time.plusSeconds(600).isAfter(Instant.now())) {

                return res
            }
        }catch (e: Exception) {
            logger.warn(e.toString())
        }

        return null
    }
}