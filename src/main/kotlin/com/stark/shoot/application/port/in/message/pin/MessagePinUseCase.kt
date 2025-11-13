package com.stark.shoot.application.port.`in`.message.pin

import com.stark.shoot.application.port.`in`.message.pin.command.PinMessageCommand
import com.stark.shoot.application.port.`in`.message.pin.command.UnpinMessageCommand
import com.stark.shoot.application.port.`in`.message.pin.result.MessagePinResult

interface MessagePinUseCase {
    fun pinMessage(command: PinMessageCommand): MessagePinResult
    fun unpinMessage(command: UnpinMessageCommand): MessagePinResult
}
