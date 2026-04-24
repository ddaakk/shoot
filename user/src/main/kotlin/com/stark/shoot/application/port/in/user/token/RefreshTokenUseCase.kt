package com.stark.shoot.application.port.`in`.user.token

import com.stark.shoot.application.dto.user.LoginResponseDto
import com.stark.shoot.application.port.`in`.user.token.command.RefreshTokenCommand

interface RefreshTokenUseCase {
    fun generateNewAccessToken(command: RefreshTokenCommand): LoginResponseDto
}
