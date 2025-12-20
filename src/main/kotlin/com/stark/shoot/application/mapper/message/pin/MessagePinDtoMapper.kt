package com.stark.shoot.application.mapper.message.pin

import com.stark.shoot.application.dto.message.pin.PinResponseDto
import com.stark.shoot.application.dto.message.pin.PinnedMessageItemDto
import com.stark.shoot.application.dto.message.pin.PinnedMessagesResponseDto
import com.stark.shoot.application.port.`in`.message.pin.result.MessagePinResult
import com.stark.shoot.application.port.`in`.message.pin.result.PinnedMessagesResult
import com.stark.shoot.domain.chat.message.ChatMessage
import com.stark.shoot.domain.chat.pin.MessagePin
import org.springframework.stereotype.Component

/**
 * MessagePin 도메인 객체를 Application DTO로 변환하는 Mapper
 *
 * Application 레이어에서 Domain → DTO 변환을 담당합니다.
 */
@Component
class MessagePinDtoMapper {

    /**
     * MessagePinResult를 PinResponseDto로 변환
     *
     * @param result 메시지 고정 결과
     * @return 메시지 고정 응답 DTO
     */
    fun toPinResponseDto(result: MessagePinResult): PinResponseDto {
        val message = result.message
        val pin = result.messagePin

        return PinResponseDto(
            messageId = message.id?.value ?: "",
            roomId = message.roomId.value,
            isPinned = pin != null,
            pinnedBy = pin?.pinnedBy?.value,
            pinnedAt = pin?.pinnedAt?.toString(),
            content = message.content.text,
            updatedAt = message.updatedAt?.toString() ?: ""
        )
    }

    /**
     * PinnedMessagesResult를 PinnedMessagesResponseDto로 변환
     *
     * @param roomId 채팅방 ID
     * @param result 고정된 메시지 목록 결과
     * @return 고정된 메시지 목록 응답 DTO
     */
    fun toPinnedMessagesResponseDto(
        roomId: Long,
        result: PinnedMessagesResult
    ): PinnedMessagesResponseDto {
        // MessagePin을 messageId로 매핑
        val pinMap = result.messagePins.associateBy { it.messageId }

        val pinnedItems = result.messages.mapNotNull { message ->
            message.id?.let { messageId ->
                pinMap[messageId]?.let { pin ->
                    toPinnedMessageItemDto(message, pin)
                }
            }
        }

        return PinnedMessagesResponseDto(
            roomId = roomId,
            pinnedMessages = pinnedItems
        )
    }

    /**
     * ChatMessage와 MessagePin을 PinnedMessageItemDto로 변환
     *
     * @param message 메시지
     * @param pin 메시지 고정 정보
     * @return 고정된 메시지 항목 DTO
     */
    private fun toPinnedMessageItemDto(
        message: ChatMessage,
        pin: MessagePin
    ): PinnedMessageItemDto {
        return PinnedMessageItemDto(
            messageId = message.id?.value ?: "",
            content = message.content.text,
            senderId = message.senderId.value,
            pinnedBy = pin.pinnedBy.value,
            pinnedAt = pin.pinnedAt.toString(),
            createdAt = message.createdAt?.toString() ?: ""
        )
    }
}
