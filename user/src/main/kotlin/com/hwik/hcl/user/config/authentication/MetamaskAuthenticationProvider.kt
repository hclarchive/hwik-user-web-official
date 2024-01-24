package com.hwik.hcl.user.config.authentication

import com.hwik.hcl.user.service.UserService
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component


@Component
class MetamaskAuthenticationProvider(
    private val userService: UserService
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication? {
        val address = authentication.name
        val hash = authentication.credentials.toString()

        val isValid = userService.checkUuid(address, hash)

        return if (isValid) {
            val user = userService.getUserByAddress(address)
            val customUserDetail = CustomUserDetails(user)

            UsernamePasswordAuthenticationToken(customUserDetail, hash, customUserDetail.authorities.toList())
        } else {
            null
        }
    }

    override fun supports(authentication: Class<*>?): Boolean {
        return authentication!!.equals(UsernamePasswordAuthenticationToken::class.java)
    }
}