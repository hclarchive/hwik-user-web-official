package com.hwik.hcl.user.util

import okhttp3.internal.toHexString
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Component
class ThymeleafUtil {
    @Value("\${web3j.token.chain-id}")
    var chainId: Long = 1L

    fun addressElipse(address: String): String {
        return address.substring(0, 6) + "..." + address.substring(address.length - 6, address.length)
    }

    fun chainId(): String {
        return "0x${chainId.toHexString()}"
    }
}