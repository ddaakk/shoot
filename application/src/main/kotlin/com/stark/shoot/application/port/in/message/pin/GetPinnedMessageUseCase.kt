package com.stark.shoot.application.port.`in`.message.pin

import com.stark.shoot.application.dto.message.pin.PinnedMessagesResponseDto
import com.stark.shoot.application.port.`in`.message.pin.command.GetPinnedMessagesCommand

interface GetPinnedMessageUseCase {
    fun getPinnedMessages(command: GetPinnedMessagesCommand): PinnedMessagesResponseDto
}
