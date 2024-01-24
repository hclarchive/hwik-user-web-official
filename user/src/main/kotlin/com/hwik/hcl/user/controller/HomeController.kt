package com.hwik.hcl.user.controller

import com.hwik.hcl.user.service.TransactionService
import com.hwik.hcl.user.service.UserService
import com.hwik.hcl.user.controller.dto.LoginDto
import com.hwik.hcl.user.util.RequestUtil
import com.hwik.hcl.user.util.UserUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody


@Controller
class HomeController(
    private val clientIPService: RequestUtil,
    private val transactionService: TransactionService,
    private val userUtil: UserUtil,
    private val userService: UserService,
    private val authenticationManager: AuthenticationManager,
    private val securityContextRepository: SecurityContextRepository,
) {
    @GetMapping("/")
    fun home(model: Model): String {
        model.addAttribute("menu", "my");

        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication !is AnonymousAuthenticationToken && authentication.isAuthenticated) {
            return "my"
        }

        return "home"
    }

    @PostMapping("/login")
    fun login(
        @RequestBody dto: LoginDto,
        request: HttpServletRequest,
        httpServletResponse: HttpServletResponse
    ): ResponseEntity<String> {
        try {
            val context = SecurityContextHolder.createEmptyContext()
            val authentication: Authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    dto.address,
                    dto.hash
                )
            )

            if (userService.updateLoginHistory(dto.address, clientIPService.getClientIP()))
            {
                context.authentication = authentication
                SecurityContextHolder.setContext(context)
                securityContextRepository.saveContext(context, request, httpServletResponse)

                return ResponseEntity.ok("Logged in successfully!")
            }
        } catch (e: Exception) {

        }
        return ResponseEntity.badRequest().body("Invalid credentials")
    }
}