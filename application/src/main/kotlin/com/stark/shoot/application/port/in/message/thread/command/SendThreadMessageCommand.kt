package com.stark.shoot.application.port.`in`.message.thread.command

import com.stark.shoot.application.dto.message.SendMessageDto

/**
 * Command for sending a thread message
 *
 * Application 레이어의 DTO를 사용하여 Adapter 레이어와 독립적입니다.
 */
data class SendThreadMessageCommand(
    val message: SendMessageDto
) {
    companion object {
        /**
         * Factory method to create a SendThreadMessageCommand
         *
         * @param message The message DTO to send as a thread message
         * @return A new SendThreadMessageCommand
         */
        fun of(message: SendMessageDto): SendThreadMessageCommand {
            return SendThreadMessageCommand(
                message = message
            )
        }
    }
}