package com.stark.shoot.application.port.`in`.user.auth

import com.stark.shoot.application.dto.user.LoginResponseDto
import com.stark.shoot.application.port.`in`.user.auth.command.LoginCommand

interface UserLoginUseCase {
    fun login(command: LoginCommand): LoginResponseDto
}
