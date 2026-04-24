package com.stark.shoot.application.dto.group

data class FriendGroupResponseDto(
    val id: Long,
    val ownerId: Long,
    val name: String,
    val description: String?,
    val memberIds: Set<Long>
)
