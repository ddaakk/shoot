package com.stark.shoot.adapter.`in`.rest.dto.user

import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.web.multipart.MultipartFile

// JSON용 요청 DTO (MultipartFile 없음)
data class CreateUserRequest(
    @field:NotBlank(message = "사용자 이름은 필수입니다")
    @field:Size(min = 3, max = 20, message = "사용자 이름은 3-20자 사이여야 합니다")
    val username: String,

    @field:NotBlank(message = "닉네임은 필수입니다")
    @field:Size(min = 2, max = 30, message = "닉네임은 2-30자 사이여야 합니다")
    val nickname: String,

    @field:NotBlank(message = "비밀번호는 필수입니다")
    @field:Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    val password: String,

    @field:NotBlank(message = "이메일은 필수입니다")
    @field:Email(message = "올바른 이메일 형식이 아닙니다")
    val email: String,

    @field:Size(max = 200, message = "자기소개는 최대 200자까지 입력 가능합니다")
    val bio: String? = null
)

// Multipart용 요청 DTO (MultipartFile 포함)
data class CreateUserMultipartRequest(
    @field:NotBlank(message = "사용자 이름은 필수입니다")
    @field:Size(min = 3, max = 20, message = "사용자 이름은 3-20자 사이여야 합니다")
    val username: String,

    @field:NotBlank(message = "닉네임은 필수입니다")
    @field:Size(min = 2, max = 30, message = "닉네임은 2-30자 사이여야 합니다")
    val nickname: String,

    @field:NotBlank(message = "비밀번호는 필수입니다")
    @field:Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    val password: String,

    @field:NotBlank(message = "이메일은 필수입니다")
    @field:Email(message = "올바른 이메일 형식이 아닙니다")
    val email: String,

    @field:Size(max = 200, message = "자기소개는 최대 200자까지 입력 가능합니다")
    val bio: String? = null,

    @field:Parameter(content = [Content(mediaType = "multipart/form-data")])
    val profileImage: MultipartFile? = null
)