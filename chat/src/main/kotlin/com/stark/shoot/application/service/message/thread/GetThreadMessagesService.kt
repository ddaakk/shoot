package com.stark.shoot.application.service.message.thread

import com.stark.shoot.application.dto.message.response.MessageResponseDto
import com.stark.shoot.application.mapper.message.MessageDtoMapper
import com.stark.shoot.application.port.`in`.message.thread.GetThreadMessagesUseCase
import com.stark.shoot.application.port.`in`.message.thread.command.GetThreadMessagesCommand
import com.stark.shoot.application.port.out.message.thread.ThreadQueryPort
import com.stark.shoot.infrastructure.annotation.UseCase

@UseCase
class GetThreadMessagesService(
    private val threadQueryPort: ThreadQueryPort,
    private val messageDtoMapper: MessageDtoMapper,
) : GetThreadMessagesUseCase {

    override fun getThreadMessages(command: GetThreadMessagesCommand): List<MessageResponseDto> {
        val messages = if (command.lastMessageId != null) {
            threadQueryPort.findByThreadIdAndBeforeId(command.threadId, command.lastMessageId, command.limit)
        } else {
            threadQueryPort.findByThreadId(command.threadId, command.limit)
        }

        return messageDtoMapper.toDtoList(messages)
    }

}
