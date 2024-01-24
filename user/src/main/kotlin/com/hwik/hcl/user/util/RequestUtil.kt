package com.hwik.hcl.user.util

import org.springframework.stereotype.Service
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes


@Service
class RequestUtil {
    fun getClientIP(): String {
        val requestAttributes = RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes
        val request = requestAttributes.request

        return request.getHeader("x-forwarded-for") ?: request.remoteAddr
    }

    fun getContextPath(): String {
        val requestAttributes = RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes
        val request = requestAttributes.request

        return request.contextPath
    }

    fun getDomain() : String
    {
        val requestAttributes = RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes
        val request = requestAttributes.request

        val scheme = request.getHeader("x-forwarded-proto") ?: request.scheme

        if (request.serverPort == 80)
        {
            return scheme + "://" + request.serverName + request.contextPath
        }

        return scheme + "://" + request.serverName + ":" + request.serverPort + request.contextPath
    }
}