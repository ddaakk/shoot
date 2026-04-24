package com.stark.shoot.application.service.message

import com.stark.shoot.application.dto.message.response.MessageResponseDto
import com.stark.shoot.application.mapper.message.MessageDtoMapper
import com.stark.shoot.application.port.`in`.message.GetMessagesUseCase
import com.stark.shoot.application.port.`in`.message.command.GetMessagesCommand
import com.stark.shoot.application.port.out.message.MessageQueryPort
import com.stark.shoot.infrastructure.annotation.UseCase

@UseCase
class GetMessagesService(
    private val messageQueryPort: MessageQueryPort,
    private val messageDtoMapper: MessageDtoMapper
) : GetMessagesUseCase {

    /**
     * 특정 채팅방의 메시지를 조회 (DTO 반환)
     *
     * @param command 메시지 조회 커맨드
     * @return 메시지 DTO 리스트
     */
    override fun getMessages(command: GetMessagesCommand): List<MessageResponseDto> {
        // 도메인 메시지 조회
        val domainMessages = if (command.lastMessageId != null) {
            messageQueryPort.findByRoomIdAndBeforeId(command.roomId, command.lastMessageId, command.limit)
        } else {
            messageQueryPort.findByRoomId(command.roomId, command.limit)
        }

        // Application DTO로 변환하여 반환
        return messageDtoMapper.toDtoList(domainMessages)
    }

}
