package com.stark.shoot.application.service.message.pin

import com.stark.shoot.application.dto.message.pin.PinnedMessagesResponseDto
import com.stark.shoot.application.mapper.message.pin.MessagePinDtoMapper
import com.stark.shoot.application.port.`in`.message.pin.GetPinnedMessageUseCase
import com.stark.shoot.application.port.`in`.message.pin.command.GetPinnedMessagesCommand
import com.stark.shoot.application.port.`in`.message.pin.result.PinnedMessagesResult
import com.stark.shoot.application.port.out.message.MessageQueryPort
import com.stark.shoot.application.port.out.message.pin.MessagePinQueryPort
import com.stark.shoot.domain.chat.vo.ChatRoomId as ChatChatRoomId
import com.stark.shoot.infrastructure.annotation.UseCase

@UseCase
class GetPinnedMessageService(
    private val messageQueryPort: MessageQueryPort,
    private val messagePinQueryPort: MessagePinQueryPort,
    private val messagePinDtoMapper: MessagePinDtoMapper
) : GetPinnedMessageUseCase {

    /**
     * 채팅방에서 고정된 메시지를 조회합니다.
     * MessagePin Aggregate를 통해 고정된 메시지 ID를 조회한 후, 메시지를 가져옵니다.
     *
     * @param command 고정 메시지 조회 커맨드
     * @return 고정된 메시지 목록 응답 DTO
     */
    override fun getPinnedMessages(
        command: GetPinnedMessagesCommand
    ): PinnedMessagesResponseDto {
        // ChatRoomId 타입 변환 (chatroom.vo -> chat.vo)
        val chatRoomId = ChatChatRoomId.from(command.roomId.value)

        // MessagePin Aggregate로 고정된 메시지 ID 목록 조회
        val messagePins = messagePinQueryPort.findAllByRoomId(chatRoomId)

        // 고정된 메시지가 없으면 빈 결과 반환
        if (messagePins.isEmpty()) {
            return messagePinDtoMapper.toPinnedMessagesResponseDto(
                command.roomId.value,
                PinnedMessagesResult(
                    messages = emptyList(),
                    messagePins = emptyList()
                )
            )
        }

        // 메시지 ID로 실제 메시지 조회
        val messages = messagePins.mapNotNull { pin ->
            messageQueryPort.findById(pin.messageId)
        }

        return messagePinDtoMapper.toPinnedMessagesResponseDto(
            command.roomId.value,
            PinnedMessagesResult(
                messages = messages,
                messagePins = messagePins
            )
        )
    }

}
