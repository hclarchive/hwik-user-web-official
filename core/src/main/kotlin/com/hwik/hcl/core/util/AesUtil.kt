package com.hwik.hcl.core.util

import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Component
class AesUtil {

    fun decrypt(encrypted: String, key: String): String {
        val ivBytes = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

        val encryptedDataBytes: ByteArray = Base64.getDecoder().decode(encrypted)
        val ivSpec = IvParameterSpec(ivBytes)
        val skeySpec = SecretKeySpec(key.toByteArray(charset("UTF-8")), "AES")
        val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec)

        val original: ByteArray = cipher.doFinal(encryptedDataBytes)

        return String(original)
    }

    fun encrypt(original: String, key: String): String {
        val ivBytes = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

        val ivSpec = IvParameterSpec(ivBytes)
        val skeySpec = SecretKeySpec(key.toByteArray(charset("UTF-8")), "AES")
        val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec)

        val encrypted: ByteArray = cipher.doFinal(original.toByteArray())

        return Base64.getEncoder().encodeToString(encrypted)
    }
}