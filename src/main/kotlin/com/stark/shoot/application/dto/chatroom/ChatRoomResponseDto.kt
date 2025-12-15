package com.stark.shoot.application.dto.chatroom

/**
 * 채팅방 응답 DTO (Application Layer)
 */
data class ChatRoomResponseDto(
    val roomId: Long,
    val title: String,
    val lastMessage: String?,
    val unreadMessages: Int,
    val isPinned: Boolean,
    val timestamp: String
)
