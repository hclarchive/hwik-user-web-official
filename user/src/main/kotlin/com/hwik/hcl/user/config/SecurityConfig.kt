package com.hwik.hcl.user.config

import com.hwik.hcl.user.config.authentication.MetamaskAuthenticationProvider
import jakarta.servlet.http.HttpSession
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.http
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.context.HttpSessionSecurityContextRepository

import org.springframework.security.web.context.SecurityContextRepository
import java.lang.RuntimeException


@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val authProvider: MetamaskAuthenticationProvider
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }


        http.authorizeHttpRequests { authorizeHttpRequests ->
            authorizeHttpRequests

                .anyRequest().permitAll()
        }.logout {
            it.logoutUrl("/logout")
            it.logoutSuccessHandler { request, response, _ ->
                val referer: String = request.getHeader("Referer")
                response.sendRedirect(referer)
            }
        }


        return http.build()
    }

    @Bean
    @Throws(RuntimeException::class)
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.getAuthenticationManager()
    }


    @Bean
    fun securityContextRepository(): SecurityContextRepository {
        return HttpSessionSecurityContextRepository()
    }

}