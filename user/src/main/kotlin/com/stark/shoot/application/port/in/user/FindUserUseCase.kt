package com.stark.shoot.application.port.`in`.user

import com.stark.shoot.application.port.`in`.user.command.FindUserByCodeCommand
import com.stark.shoot.application.port.`in`.user.command.FindUserByIdCommand
import com.stark.shoot.application.port.`in`.user.command.FindUserByUsernameCommand
import com.stark.shoot.domain.user.User
import com.stark.shoot.domain.user.vo.Nickname

interface FindUserUseCase {
    fun findById(command: FindUserByIdCommand): User?
    fun findByUsername(command: FindUserByUsernameCommand): User?
    fun findByUserCode(command: FindUserByCodeCommand): User?
    fun existsByNickname(nickname: Nickname): Boolean
}
