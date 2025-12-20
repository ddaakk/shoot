package com.stark.shoot.application.mapper.friend

import com.stark.shoot.application.dto.friend.FriendResponseDto
import com.stark.shoot.domain.user.User
import org.springframework.stereotype.Component

/**
 * Friend 도메인 객체를 Application DTO로 변환하는 Mapper
 *
 * Application 레이어에서 Domain → DTO 변환을 담당합니다.
 */
@Component
class FriendDtoMapper {

    /**
     * User 도메인 객체를 FriendResponseDto로 변환
     *
     * @param user 사용자 도메인 객체
     * @return 친구 응답 DTO
     */
    fun toDto(user: User): FriendResponseDto {
        return FriendResponseDto(
            id = user.id?.value ?: 0L,
            username = user.username.value,
            nickname = user.nickname.value,
            profileImageUrl = user.profileImageUrl?.value
        )
    }

    /**
     * User 도메인 객체 리스트를 FriendResponseDto 리스트로 변환
     *
     * @param users 사용자 도메인 객체 리스트
     * @return 친구 응답 DTO 리스트
     */
    fun toDtoList(users: List<User>): List<FriendResponseDto> {
        return users.map { toDto(it) }
    }
}
