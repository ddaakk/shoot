package com.stark.shoot.application.port.`in`.user.group

import com.stark.shoot.application.dto.group.FriendGroupResponseDto
import com.stark.shoot.application.port.`in`.user.group.command.*

interface ManageFriendGroupUseCase {
    fun createGroup(command: CreateGroupCommand): FriendGroupResponseDto
    fun renameGroup(command: RenameGroupCommand): FriendGroupResponseDto
    fun updateDescription(command: UpdateDescriptionCommand): FriendGroupResponseDto
    fun addMember(command: AddMemberCommand): FriendGroupResponseDto
    fun removeMember(command: RemoveMemberCommand): FriendGroupResponseDto
    fun deleteGroup(command: DeleteGroupCommand)
}
