package com.stark.shoot.chatroom

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.stark.shoot"])
class ChatRoomServiceApplication

fun main(args: Array<String>) {
    runApplication<ChatRoomServiceApplication>(*args)
}
