package com.stark.shoot.application.service.user.friend

import com.stark.shoot.application.dto.friend.FriendResponseDto
import com.stark.shoot.application.mapper.friend.FriendDtoMapper
import com.stark.shoot.application.port.`in`.user.friend.FriendSearchUseCase
import com.stark.shoot.application.port.`in`.user.friend.command.SearchFriendsCommand
import com.stark.shoot.application.port.out.user.UserQueryPort
import com.stark.shoot.application.port.out.user.friend.relate.FriendshipQueryPort
import com.stark.shoot.application.port.out.user.friend.request.FriendRequestQueryPort
import com.stark.shoot.domain.social.type.FriendRequestStatus
import com.stark.shoot.domain.shared.UserId
import com.stark.shoot.infrastructure.annotation.UseCase
import com.stark.shoot.infrastructure.exception.web.ResourceNotFoundException

@UseCase
class FriendSearchService(
    private val userQueryPort: UserQueryPort,
    private val friendshipQueryPort: FriendshipQueryPort,
    private val friendRequestQueryPort: FriendRequestQueryPort,
    private val friendDtoMapper: FriendDtoMapper
) : FriendSearchUseCase {

    /**
     * 잠재적 친구 검색
     *
     * @param command 친구 검색 커맨드
     * @return 친구 목록
     */
    override fun searchPotentialFriends(command: SearchFriendsCommand): List<FriendResponseDto> {
        val userId = command.userId
        val query = command.query

        // 사용자 존재 여부 확인
        if (!userQueryPort.existsById(userId)) {
            throw ResourceNotFoundException("사용자를 찾을 수 없습니다: $userId")
        }

        // 제외할 사용자 목록: 본인, 이미 친구, 받은/보낸 친구 요청 대상
        val excludedIds = mutableSetOf<UserId>().apply {
            // 본인 추가
            add(userId)

            // 친구 목록 추가
            friendshipQueryPort.findAllFriendships(userId).forEach {
                add(it.friendId)
            }

            // 받은 친구 요청 추가
            friendRequestQueryPort.findAllReceivedRequests(
                receiverId = userId,
                status = FriendRequestStatus.PENDING
            ).forEach {
                add(it.senderId)
            }

            // 보낸 친구 요청 추가
            friendRequestQueryPort.findAllSentRequests(
                senderId = userId,
                status = FriendRequestStatus.PENDING
            ).forEach {
                add(it.receiverId)
            }
        }

        // DB 레벨에서 검색어로 사용자 검색 (제외 목록 포함)
        val searchedUsers = userQueryPort.searchUsers(query, excludedIds)

        // 응답 DTO로 변환
        return friendDtoMapper.toDtoList(searchedUsers)
    }

}
