package com.stark.shoot.application.port.`in`.user.group

import com.stark.shoot.application.dto.group.FriendGroupResponseDto
import com.stark.shoot.application.port.`in`.user.group.command.GetGroupCommand
import com.stark.shoot.application.port.`in`.user.group.command.GetGroupsCommand

interface FindFriendGroupUseCase {
    fun getGroup(command: GetGroupCommand): FriendGroupResponseDto?
    fun getGroups(command: GetGroupsCommand): List<FriendGroupResponseDto>
}
