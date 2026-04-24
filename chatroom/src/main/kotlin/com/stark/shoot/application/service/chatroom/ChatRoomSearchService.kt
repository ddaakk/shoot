package com.stark.shoot.application.service.chatroom

import com.stark.shoot.application.dto.chatroom.ChatRoomResponseDto
import com.stark.shoot.application.mapper.chatroom.ChatRoomDtoMapper
import com.stark.shoot.application.port.`in`.chatroom.ChatRoomSearchUseCase
import com.stark.shoot.application.port.`in`.chatroom.command.SearchChatRoomsCommand
import com.stark.shoot.application.port.out.chatroom.ChatRoomQueryPort
import com.stark.shoot.domain.chatroom.service.ChatRoomDomainService
import com.stark.shoot.infrastructure.annotation.UseCase

@UseCase
class ChatRoomSearchService(
    private val chatRoomQueryPort: ChatRoomQueryPort,
    private val chatRoomDomainService: ChatRoomDomainService,
    private val chatRoomDtoMapper: ChatRoomDtoMapper,
) : ChatRoomSearchUseCase {

    /**
     * 채팅방 검색
     *
     * @param command 채팅방 검색 커맨드
     * @return ChatRoomResponseDto 채팅방 목록
     */
    override fun searchChatRooms(command: SearchChatRoomsCommand): List<ChatRoomResponseDto> {
        val userId = command.userId
        val query = command.query
        val type = command.type
        val unreadOnly = command.unreadOnly

        // 사용자가 참여한 채팅방 목록을 조회
        val chatRooms = chatRoomQueryPort.findByParticipantId(userId)

        // 필터링된 채팅방 목록
        val filteredRooms = chatRoomDomainService.filterChatRooms(chatRooms, query, type, unreadOnly)

        // 채팅방 정보 준비
        val titles = chatRoomDomainService.prepareChatRoomTitles(filteredRooms, userId)
        val lastMessages = chatRoomDomainService.prepareLastMessages(filteredRooms)
        val timestamps = chatRoomDomainService.prepareTimestamps(filteredRooms)

        // ChatRoomResponseDto로 변환하여 반환
        return chatRoomDtoMapper.toResponseList(filteredRooms, userId, titles, lastMessages, timestamps)
    }
}
