package com.stark.shoot.application.mapper.group

import com.stark.shoot.application.dto.group.FriendGroupResponseDto
import com.stark.shoot.domain.social.FriendGroup
import org.springframework.stereotype.Component

@Component
class FriendGroupDtoMapper {

    fun toDto(group: FriendGroup): FriendGroupResponseDto = FriendGroupResponseDto(
        id = group.id?.value ?: 0L,
        ownerId = group.ownerId.value,
        name = group.name.value,
        description = group.description,
        memberIds = group.memberIds.map { it.value }.toSet()
    )

    fun toDtoList(groups: List<FriendGroup>): List<FriendGroupResponseDto> =
        groups.map { toDto(it) }

}
