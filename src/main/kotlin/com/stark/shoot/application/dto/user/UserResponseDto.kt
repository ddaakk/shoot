package com.stark.shoot.application.dto.user

import com.stark.shoot.domain.user.type.UserStatus
import java.time.Instant

/**
 * 사용자 응답 DTO (Application Layer)
 */
data class UserResponseDto(
    val id: String,
    val username: String,
    val nickname: String,
    val status: UserStatus,
    val profileImageUrl: String?,
    val backgroundImageUrl: String?,
    val bio: String?,
    val userCode: String,
    val lastSeenAt: Instant?
)
