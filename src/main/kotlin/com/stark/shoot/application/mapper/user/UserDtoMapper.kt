package com.stark.shoot.application.mapper.user

import com.stark.shoot.application.dto.user.UserResponseDto
import com.stark.shoot.domain.user.User
import org.springframework.stereotype.Component

/**
 * User 도메인 객체를 Application DTO로 변환하는 Mapper
 *
 * Application 레이어에서 Domain → DTO 변환을 담당합니다.
 */
@Component
class UserDtoMapper {

    /**
     * User 도메인 객체를 UserResponseDto로 변환
     */
    fun toDto(user: User): UserResponseDto {
        return UserResponseDto(
            id = user.id.toString(),
            username = user.username.value,
            nickname = user.nickname.value,
            status = user.status,
            profileImageUrl = user.profileImageUrl?.value,
            backgroundImageUrl = user.backgroundImageUrl?.value,
            bio = user.bio?.value,
            userCode = user.userCode.value,
            lastSeenAt = user.lastSeenAt
        )
    }
}
