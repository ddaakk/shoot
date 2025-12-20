package com.stark.shoot.application.dto.message.reaction

/**
 * 리액션 목록 응답 DTO (Application Layer)
 */
data class ReactionListResponseDto(
    val messageId: String,
    val reactions: Map<String, Set<Long>>
)
