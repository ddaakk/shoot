package com.stark.shoot.application.port.`in`.user.friend

import com.stark.shoot.application.dto.friend.FriendResponseDto
import com.stark.shoot.application.port.`in`.user.friend.command.GetRecommendedFriendsCommand

interface RecommendFriendsUseCase {
    fun getRecommendedFriends(command: GetRecommendedFriendsCommand): List<FriendResponseDto>
}
