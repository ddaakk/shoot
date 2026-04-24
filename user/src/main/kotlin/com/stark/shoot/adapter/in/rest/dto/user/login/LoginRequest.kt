package com.stark.shoot.adapter.`in`.rest.dto.user.login

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * 사용자 로그인 요청
 */
data class LoginRequest(
    @field:NotBlank(message = "사용자 이름은 필수입니다")
    @field:Size(min = 3, max = 20, message = "사용자 이름은 3-20자 사이여야 합니다")
    val username: String,

    @field:NotBlank(message = "비밀번호는 필수입니다")
    @field:Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    val password: String
)