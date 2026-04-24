package com.stark.shoot.social

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.stark.shoot"])
class SocialServiceApplication

fun main(args: Array<String>) {
    runApplication<SocialServiceApplication>(*args)
}
