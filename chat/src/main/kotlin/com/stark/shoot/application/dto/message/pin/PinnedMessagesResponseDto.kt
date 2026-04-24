package com.stark.shoot.application.dto.message.pin

/**
 * 고정된 메시지 목록 응답 DTO (Application Layer)
 */
data class PinnedMessagesResponseDto(
    val roomId: Long,
    val pinnedMessages: List<PinnedMessageItemDto>
)
