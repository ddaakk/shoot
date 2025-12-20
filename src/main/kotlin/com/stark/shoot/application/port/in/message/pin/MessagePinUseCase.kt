package com.stark.shoot.application.port.`in`.message.pin

import com.stark.shoot.application.dto.message.pin.PinResponseDto
import com.stark.shoot.application.port.`in`.message.pin.command.PinMessageCommand
import com.stark.shoot.application.port.`in`.message.pin.command.UnpinMessageCommand

interface MessagePinUseCase {
    fun pinMessage(command: PinMessageCommand): PinResponseDto
    fun unpinMessage(command: UnpinMessageCommand): PinResponseDto
}
