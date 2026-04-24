package com.stark.shoot.application.dto.message.pin

/**
 * 메시지 고정/고정 해제 응답 DTO (Application Layer)
 */
data class PinResponseDto(
    val messageId: String,
    val roomId: Long,
    val isPinned: Boolean,
    val pinnedBy: Long?,
    val pinnedAt: String?,
    val content: String,
    val updatedAt: String
)
