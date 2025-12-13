package com.stark.shoot.application.port.`in`.message.command

import com.stark.shoot.application.dto.message.SendMessageDto

/**
 * Command for sending a message
 *
 * Application 레이어의 DTO를 사용하여 Adapter 레이어와 독립적입니다.
 */
data class SendMessageCommand(
    val message: SendMessageDto
) {
    companion object {
        /**
         * Factory method to create a SendMessageCommand
         *
         * @param message The message DTO to send
         * @return A new SendMessageCommand
         */
        fun of(message: SendMessageDto): SendMessageCommand {
            return SendMessageCommand(
                message = message
            )
        }
    }
}