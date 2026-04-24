package com.stark.shoot.application.mapper.chatroom

import com.stark.shoot.application.dto.chatroom.ChatRoomResponseDto
import com.stark.shoot.domain.chatroom.ChatRoom
import com.stark.shoot.domain.shared.UserId
import org.springframework.stereotype.Component

/**
 * ChatRoom 도메인 객체를 Application DTO로 변환하는 Mapper
 *
 * Application 레이어에서 Domain → DTO 변환을 담당합니다.
 */
@Component
class ChatRoomDtoMapper {

    /**
     * 채팅방 목록을 응답 DTO 목록으로 변환
     *
     * @param rooms 채팅방 목록
     * @param userId 사용자 ID
     * @param titles 채팅방 제목 맵 (roomId -> title)
     * @param lastMessages 마지막 메시지 맵 (roomId -> message)
     * @param timestamps 타임스탬프 맵 (roomId -> formatted timestamp)
     * @return 채팅방 응답 DTO 목록
     */
    fun toResponseList(
        rooms: List<ChatRoom>,
        userId: UserId,
        titles: Map<Long, String>,
        lastMessages: Map<Long, String>,
        timestamps: Map<Long, String>
    ): List<ChatRoomResponseDto> {
        return rooms.map { room ->
            toDto(
                room, userId,
                titles[room.id?.value] ?: "채팅방",
                lastMessages[room.id?.value] ?: "메시지 없음",
                timestamps[room.id?.value] ?: ""
            )
        }
    }

    /**
     * 채팅방을 응답 DTO로 변환
     *
     * DDD 개선: isPinned는 ChatRoomFavorite Aggregate에서 관리됨
     *
     * @param room 채팅방
     * @param userId 사용자 ID
     * @param title 채팅방 제목
     * @param lastMessage 마지막 메시지
     * @param timestamp 포맷된 타임스탬프
     * @param isPinned 즐겨찾기 여부 (호출자가 ChatRoomFavorite에서 조회하여 제공)
     * @return 채팅방 응답 DTO
     */
    fun toDto(
        room: ChatRoom,
        userId: UserId,
        title: String,
        lastMessage: String,
        timestamp: String,
        isPinned: Boolean = false
    ): ChatRoomResponseDto {
        return ChatRoomResponseDto(
            roomId = room.id?.value ?: 0L,
            title = title,
            lastMessage = lastMessage,
            unreadMessages = 0, // 실제 구현시 읽지 않은 메시지 수 계산 로직 추가
            isPinned = isPinned,
            timestamp = timestamp
        )
    }

    /**
     * 채팅방을 간단한 응답 DTO로 변환 (새로 생성된 채팅방용)
     *
     * @param room 채팅방
     * @param userId 사용자 ID
     * @param isPinned 즐겨찾기 여부
     * @return 채팅방 응답 DTO
     */
    fun toSimpleDto(
        room: ChatRoom,
        userId: Long,
        isPinned: Boolean = false
    ): ChatRoomResponseDto {
        val title = room.title?.value ?: "채팅방"
        val timestamp = java.time.format.DateTimeFormatter
            .ofPattern("a h:mm")
            .format(room.lastActiveAt.atZone(java.time.ZoneId.systemDefault()))

        return ChatRoomResponseDto(
            roomId = room.id?.value ?: 0L,
            title = title,
            lastMessage = null,
            unreadMessages = 0,
            isPinned = isPinned,
            timestamp = timestamp
        )
    }
}
