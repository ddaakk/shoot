package com.stark.shoot.application.mapper.message.schedule

import com.stark.shoot.application.dto.message.schedule.ScheduledMessageContentDto
import com.stark.shoot.application.dto.message.schedule.ScheduledMessageResponseDto
import com.stark.shoot.domain.chat.message.ScheduledMessage
import org.springframework.stereotype.Component

/**
 * 예약 메시지 도메인 객체를 Application DTO로 변환하는 Mapper
 *
 * Application 레이어에서 Domain → DTO 변환을 담당합니다.
 */
@Component
class ScheduledMessageDtoMapper {

    /**
     * ScheduledMessage 도메인 객체를 ScheduledMessageResponseDto로 변환
     */
    fun toDto(domain: ScheduledMessage): ScheduledMessageResponseDto {
        val messageId = domain.id?.toString()
            ?: throw IllegalStateException("Scheduled message ID should not be null when converting to response")

        return ScheduledMessageResponseDto(
            id = messageId,
            roomId = domain.roomId,
            senderId = domain.senderId,
            content = ScheduledMessageContentDto(
                text = domain.content.text,
                type = domain.content.type,
                isEdited = domain.content.isEdited,
                isDeleted = domain.content.isDeleted
            ),
            scheduledAt = domain.scheduledAt,
            createdAt = domain.createdAt,
            status = domain.status
        )
    }

    /**
     * ScheduledMessage 리스트를 DTO 리스트로 변환
     */
    fun toDtoList(messages: List<ScheduledMessage>): List<ScheduledMessageResponseDto> {
        return messages.map { toDto(it) }
    }
}
