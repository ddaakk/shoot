package com.stark.shoot.application.dto.message.pin

/**
 * 고정된 메시지 항목 DTO (Application Layer)
 */
data class PinnedMessageItemDto(
    val messageId: String,
    val content: String,
    val senderId: Long,
    val pinnedBy: Long?,
    val pinnedAt: String?,
    val createdAt: String
)
