package com.hwik.hcl.user

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@ComponentScan(basePackages = ["com.hwik.hcl.user", "com.hwik.hcl.core"])
class UserApplication

fun main(args: Array<String>) {
    runApplication<UserApplication>(*args)
}
