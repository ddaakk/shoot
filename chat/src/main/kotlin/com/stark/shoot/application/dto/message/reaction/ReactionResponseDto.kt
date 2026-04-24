package com.stark.shoot.application.dto.message.reaction

/**
 * 메시지 리액션 응답 DTO (Application Layer)
 */
data class ReactionResponseDto(
    val messageId: String,
    val reactions: List<ReactionInfoDto>,
    val updatedAt: String
)
