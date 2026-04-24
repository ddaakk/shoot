package com.stark.shoot.adapter.`in`.rest.user

import com.stark.shoot.adapter.`in`.rest.dto.user.login.LoginRequest
import com.stark.shoot.application.dto.user.LoginResponseDto
import com.stark.shoot.application.dto.user.UserResponseDto
import com.stark.shoot.application.port.`in`.user.auth.UserAuthUseCase
import com.stark.shoot.application.port.`in`.user.auth.UserLoginUseCase
import com.stark.shoot.application.port.`in`.user.auth.command.LoginCommand
import com.stark.shoot.application.port.`in`.user.auth.command.RetrieveUserDetailsCommand
import com.stark.shoot.domain.user.type.UserStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.security.core.Authentication
import java.time.Instant

@DisplayName("AuthController 단위 테스트")
class AuthControllerTest {

    private val userLoginUseCase = mock(UserLoginUseCase::class.java)
    private val userAuthUseCase = mock(UserAuthUseCase::class.java)
    private val authentication = mock(Authentication::class.java)
    private val controller = AuthController(userLoginUseCase, userAuthUseCase)

    @Test
    @DisplayName("[happy] 사용자 로그인을 처리한다")
    fun `사용자 로그인을 처리한다`() {
        // given
        val request = LoginRequest("testuser", "Password123!")
        val command = LoginCommand.of("testuser", "Password123!")
        val loginResponseDto = LoginResponseDto(
            userId = "1",
            accessToken = "jwt.token.here",
            refreshToken = "refresh.token.here"
        )

        `when`(userLoginUseCase.login(command)).thenReturn(loginResponseDto)

        // when
        val response = controller.login(request)

        // then
        assertThat(response).isNotNull
        assertThat(response.success).isTrue()
        assertThat(response.data?.accessToken).isEqualTo("jwt.token.here")
        assertThat(response.data?.refreshToken).isEqualTo("refresh.token.here")
        assertThat(response.data?.userId).isEqualTo("1")
        assertThat(response.message).isEqualTo("로그인에 성공했습니다.")

        verify(userLoginUseCase).login(command)
    }

    @Test
    @DisplayName("[happy] 현재 사용자 정보를 조회한다")
    fun `현재 사용자 정보를 조회한다`() {
        // given
        val userResponseDto = UserResponseDto(
            id = "1",
            username = "testuser",
            nickname = "테스트 유저",
            status = UserStatus.ONLINE,
            profileImageUrl = "http://example.com/profile.jpg",
            backgroundImageUrl = "http://example.com/background.jpg",
            bio = "자기소개",
            userCode = "USER123",
            lastSeenAt = Instant.now()
        )

        val command = RetrieveUserDetailsCommand.of(authentication)

        `when`(userAuthUseCase.retrieveUserDetails(command)).thenReturn(userResponseDto)

        // when
        val response = controller.getCurrentUser(authentication)

        // then
        assertThat(response).isNotNull
        assertThat(response.success).isTrue()
        assertThat(response.data?.id).isEqualTo("1")
        assertThat(response.data?.username).isEqualTo("testuser")
        assertThat(response.data?.nickname).isEqualTo("테스트 유저")

        verify(userAuthUseCase).retrieveUserDetails(command)
    }
}
