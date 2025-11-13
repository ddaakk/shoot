package com.stark.shoot.application.port.`in`.message.pin

import com.stark.shoot.application.port.`in`.message.pin.command.GetPinnedMessagesCommand
import com.stark.shoot.application.port.`in`.message.pin.result.PinnedMessagesResult

interface GetPinnedMessageUseCase {
    fun getPinnedMessages(command: GetPinnedMessagesCommand): PinnedMessagesResult
}
