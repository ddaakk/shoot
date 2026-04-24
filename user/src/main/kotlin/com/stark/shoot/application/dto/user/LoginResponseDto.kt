package com.stark.shoot.application.dto.user

/**
 * 로그인 응답 DTO (Application Layer)
 */
data class LoginResponseDto(
    val userId: String,
    val accessToken: String,
    val refreshToken: String
)
