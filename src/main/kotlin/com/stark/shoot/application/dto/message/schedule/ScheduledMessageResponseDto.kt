package com.stark.shoot.application.dto.message.schedule

import com.stark.shoot.domain.chat.message.type.MessageType
import com.stark.shoot.domain.chat.message.type.ScheduledMessageStatus
import java.time.Instant

/**
 * 예약 메시지 응답 DTO (Application Layer)
 */
data class ScheduledMessageResponseDto(
    val id: String,
    val roomId: Long,
    val senderId: Long,
    val content: ScheduledMessageContentDto,
    val scheduledAt: Instant,
    val createdAt: Instant,
    val status: ScheduledMessageStatus
)

/**
 * 예약 메시지 컨텐츠 DTO
 */
data class ScheduledMessageContentDto(
    val text: String,
    val type: MessageType,
    val isEdited: Boolean = false,
    val isDeleted: Boolean = false
)
