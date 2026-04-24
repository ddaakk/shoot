package com.stark.shoot.application.port.`in`.user.auth

import com.stark.shoot.application.dto.user.UserResponseDto
import com.stark.shoot.application.port.`in`.user.auth.command.RetrieveUserDetailsCommand

interface UserAuthUseCase {
    fun retrieveUserDetails(command: RetrieveUserDetailsCommand): UserResponseDto
}
