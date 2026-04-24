package com.stark.shoot.application.port.`in`.message

import com.stark.shoot.application.dto.message.response.MessageResponseDto
import com.stark.shoot.application.port.`in`.message.command.GetMessagesCommand

interface GetMessagesUseCase {
    fun getMessages(command: GetMessagesCommand): List<MessageResponseDto>
}
