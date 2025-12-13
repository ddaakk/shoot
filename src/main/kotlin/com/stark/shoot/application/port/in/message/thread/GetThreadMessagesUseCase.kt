package com.stark.shoot.application.port.`in`.message.thread

import com.stark.shoot.application.dto.message.response.MessageResponseDto
import com.stark.shoot.application.port.`in`.message.thread.command.GetThreadMessagesCommand

interface GetThreadMessagesUseCase {
    fun getThreadMessages(command: GetThreadMessagesCommand): List<MessageResponseDto>
}
