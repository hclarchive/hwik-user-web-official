package com.hwik.hcl.user.util

import com.hwik.hcl.core.entity.UserEntity
import com.hwik.hcl.user.config.authentication.CustomUserDetails
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class UserUtil {
    fun getUser() : UserEntity? {
        val authentication = SecurityContextHolder.getContext().authentication
        val userDetails = authentication.principal as? CustomUserDetails

        return userDetails?.user
    }
}