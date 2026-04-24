package com.stark.shoot.application.dto.message.reaction

/**
 * 리액션 정보 DTO (Application Layer)
 */
data class ReactionInfoDto(
    val reactionType: String,
    val emoji: String,
    val description: String,
    val userIds: List<Long>,
    val count: Int
)
