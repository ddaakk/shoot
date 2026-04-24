package com.stark.shoot.application.dto.friend

/**
 * 친구 응답 DTO (Application Layer)
 *
 * Application 레이어에서 도메인 객체를 변환하여 반환하는 DTO
 */
data class FriendResponseDto(
    val id: Long,
    val username: String,
    val nickname: String,
    val profileImageUrl: String?
)
