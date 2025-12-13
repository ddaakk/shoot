package com.stark.shoot.adapter.`in`.rest.user

import com.stark.shoot.adapter.`in`.rest.dto.ResponseDto
import com.stark.shoot.adapter.`in`.rest.dto.user.CreateUserRequest
import com.stark.shoot.adapter.`in`.rest.dto.user.CreateUserMultipartRequest
import com.stark.shoot.adapter.`in`.rest.dto.user.UserResponse
import com.stark.shoot.adapter.`in`.rest.dto.user.toResponse
import com.stark.shoot.application.port.`in`.user.UserCreateUseCase
import com.stark.shoot.application.port.`in`.user.UserDeleteUseCase
import com.stark.shoot.application.port.`in`.user.command.CreateUserCommand
import com.stark.shoot.application.port.`in`.user.command.DeleteUserCommand
import com.stark.shoot.infrastructure.util.extractUserIdAsLong
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@Tag(name = "사용자", description = "사용자 관련 API")
@RequestMapping("/api/v1/users")
@RestController
class UserController(
    private val userCreateUseCase: UserCreateUseCase,
    private val userDeleteUseCase: UserDeleteUseCase,
    private val findUserUseCase: com.stark.shoot.application.port.`in`.user.FindUserUseCase
) {

    @Operation(
        summary = "사용자 생성 (회원가입 - JSON)",
        description = "JSON 형식으로 새로운 사용자를 생성합니다."
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createUser(@Valid @RequestBody request: CreateUserRequest): ResponseDto<UserResponse> {
        val command = CreateUserCommand.of(request)
        val user = userCreateUseCase.createUser(command)
        return ResponseDto.success(user.toResponse(), "회원가입이 완료되었습니다.")
    }

    @Operation(
        summary = "사용자 생성 (회원가입 - Multipart)",
        description = "Multipart 형식으로 새로운 사용자를 생성합니다. 프로필 이미지 포함 가능."
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createUserWithImage(@Valid @ModelAttribute request: CreateUserMultipartRequest): ResponseDto<UserResponse> {
        // CreateUserMultipartRequest를 CreateUserRequest로 변환하여 기존 로직 재사용
        val simpleRequest = CreateUserRequest(
            username = request.username,
            nickname = request.nickname,
            password = request.password,
            email = request.email,
            bio = request.bio
        )
        val command = CreateUserCommand.of(simpleRequest)
        val user = userCreateUseCase.createUser(command)
        // TODO: request.profileImage 처리 로직 추가 필요
        return ResponseDto.success(user.toResponse(), "회원가입이 완료되었습니다.")
    }

    @Operation(
        summary = "회원 탈퇴",
        description = """
           - 현재 사용자를 탈퇴 처리합니다.
             - Authentication 객체를 매개변수로 받는 이유는, 이 API가 현재 로그인한 사용자의 정보를 기반으로 동작해야 하기 때문입니다.
             - 여기서 Authentication은 Spring Security가 제공하는 인터페이스로, 인증된 사용자의 세부 정보(예: 사용자 ID, 권한 등)를 담고 있습니다.
        """
    )
    @DeleteMapping("/me")
    fun deleteUser(
        authentication: Authentication
    ): ResponseDto<Unit> {
        val userId = authentication.extractUserIdAsLong()
        val command = DeleteUserCommand.of(userId)
        userDeleteUseCase.deleteUser(command)
        return ResponseDto.success(Unit, "회원 탈퇴가 완료되었습니다.")
    }

    @Operation(
        summary = "사용자 이름 중복 체크",
        description = "회원가입 시 사용자 이름(username)이 이미 존재하는지 확인합니다."
    )
    @GetMapping("/check/username/{username}")
    fun checkUsernameAvailability(@PathVariable username: String): ResponseDto<Map<String, Boolean>> {
        val command = com.stark.shoot.application.port.`in`.user.command.FindUserByUsernameCommand.of(username)
        val user = findUserUseCase.findByUsername(command)
        val available = user == null
        return ResponseDto.success(
            mapOf("available" to available),
            if (available) "사용 가능한 사용자 이름입니다." else "이미 사용 중인 사용자 이름입니다."
        )
    }

    @Operation(
        summary = "닉네임 중복 체크",
        description = "회원가입 시 닉네임(nickname)이 이미 존재하는지 확인합니다."
    )
    @GetMapping("/check/nickname/{nickname}")
    fun checkNicknameAvailability(@PathVariable nickname: String): ResponseDto<Map<String, Boolean>> {
        val nicknameVO = com.stark.shoot.domain.user.vo.Nickname.from(nickname)
        val exists = findUserUseCase.existsByNickname(nicknameVO)
        val available = !exists

        return if (available) {
            ResponseDto.success(
                mapOf("available" to true),
                "사용 가능한 닉네임입니다."
            )
        } else {
            ResponseDto.success(
                mapOf("available" to false),
                "이미 사용 중인 닉네임입니다."
            )
        }
    }

}
