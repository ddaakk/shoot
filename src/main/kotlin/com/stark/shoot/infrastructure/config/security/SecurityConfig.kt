package com.stark.shoot.infrastructure.config.security

import com.stark.shoot.infrastructure.config.jwt.JwtProvider
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties(CorsProperties::class)
@ConditionalOnProperty(name = ["security.enabled"], havingValue = "true", matchIfMissing = true)
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthFilter,
    private val jwtProvider: JwtProvider,
    private val corsProperties: CorsProperties
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder() // BCrypt 알고리즘을 사용한 PasswordEncoder
    }

    /**
     * principal이 String(= JWT 토큰) 형태라고 가정하고,
     * validateToken()만 통과하면 인증을 허용하는 간단한 AuthenticationManager 예시.
     */
    @Bean
    fun authenticationManager(): AuthenticationManager {
        return AuthenticationManager { authentication ->
            val principal = authentication.principal
            if (principal is String) {
                // 여기서 토큰이 유효한지 다시 한번 확인
                jwtProvider.isTokenValid(principal)
                authentication.isAuthenticated = true
                return@AuthenticationManager authentication
            }
            throw AuthenticationServiceException("Unsupported authentication type")
        }
    }

    /**
     * CORS 설정
     * - application.yml의 cors 설정을 사용
     * - 환경별로 다른 설정 가능 (local, dev, prod)
     * - 빈 문자열은 필터링하여 제외
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            // 빈 문자열을 제외하고 설정
            allowedOrigins = corsProperties.allowedOrigins.filter { it.isNotBlank() }
            allowedMethods = corsProperties.allowedMethods
            allowedHeaders = corsProperties.allowedHeaders
            exposedHeaders = corsProperties.exposedHeaders
            allowCredentials = corsProperties.allowCredentials
            maxAge = corsProperties.maxAge
        }

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    /**
     * CSRF 보호가 필요한지 결정하는 메서드
     * - REST API는 일반적으로 CSRF 보호가 필요하지 않음 (상태를 저장하지 않고 토큰 기반 인증 사용)
     * - 브라우저 기반 폼 제출이 있는 경우 CSRF 보호 활성화 고려
     */
    private fun shouldEnableCsrf(): Boolean {
        // 현재는 JWT 토큰 기반 인증만 사용하므로 CSRF 보호 비활성화
        // 향후 쿠키 기반 인증이나 폼 제출이 추가되면 true로 변경 고려
        return false
    }

    /**
     * JWT 토큰 검증 필터를 SecurityFilterChain에 등록합니다.
     * - CSRF 보안 기능 비활성화
     * - 세션 관리 정책: STATELESS
     * - 인증이 필요한 요청에 대해 JWT 토큰 검증 필터를 추가
     * - 인증 실패 시 401 Unauthorized 응답 반환
     * - WebSocket 연결 엔드포인트는 인증 없이 접근 가능
     * - JWT 토큰 검증 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
     * - 인증 실패 처리: 401 Unauthorized 응답 반환
     */
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            // CSRF 보호 설정
            // JWT 토큰 기반 인증을 사용하는 REST API는 일반적으로 CSRF 공격에 취약하지 않음
            // 클라이언트가 쿠키를 사용하여 인증하는 경우 CSRF 보호를 활성화해야 함
            .csrf { csrf -> 
                if (shouldEnableCsrf()) {
                    // CSRF 보호가 필요한 경우 특정 경로만 보호
                    csrf.ignoringRequestMatchers("/api/v1/auth/login", "/api/v1/users")
                        .ignoringRequestMatchers("/ws/**")
                } else {
                    csrf.disable()
                }
            }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                // 인증이 필요하지 않은 경로
                // 로그인, 회원가입 등은 permitAll
                it.requestMatchers("/api/v1/auth/login", "/api/v1/users", "/api/v1/chatrooms/updates/**").permitAll()
                it.requestMatchers("/api/v1/users/check/**").permitAll() // 중복 체크 API
                it.requestMatchers("/ws/**").permitAll()
                it.requestMatchers("/api/v1/auth/refresh-token").permitAll()

                // Swagger UI 경로 허용 (개발 환경에서만 사용 권장)
                // TODO: 프로덕션 환경에서는 Swagger 비활성화 또는 인증 필요
                it.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()

                // Admin API - 인증 및 ADMIN 권한 필요
                it.requestMatchers("/api/admin/**").hasRole("ADMIN")

                it.requestMatchers("/api/v1/users/me").authenticated()
                it.requestMatchers("/api/v1/messages/mark-read").authenticated() // 명시적 인증 필요
                it.anyRequest().authenticated()
            }
            // JwtAuthFilter를 UsernamePasswordAuthenticationFilter보다 앞단에 추가
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling { ex ->
                // 인증 실패 시 401 반환 (더 자세한 오류 메시지 포함)
                ex.authenticationEntryPoint { request, response, authException ->
                    // 로깅 추가
                    val logger = org.slf4j.LoggerFactory.getLogger("SecurityExceptionHandler")
                    logger.warn("Authentication failure: ${authException.message} for ${request.requestURI}")

                    // JSON 형식으로 오류 응답 반환
                    response.contentType = "application/json;charset=UTF-8"
                    response.status = HttpServletResponse.SC_UNAUTHORIZED

                    val errorMessage = when (authException) {
                        is org.springframework.security.authentication.BadCredentialsException -> 
                            "Invalid credentials"
                        is org.springframework.security.authentication.InsufficientAuthenticationException -> 
                            "Authentication required"
                        else -> "Authentication failed: ${authException.message}"
                    }

                    val errorJson = """
                        {
                            "timestamp": "${java.time.Instant.now()}",
                            "status": 401,
                            "error": "Unauthorized",
                            "message": "$errorMessage",
                            "path": "${request.requestURI}"
                        }
                    """.trimIndent()

                    response.writer.write(errorJson)
                }

                // 접근 거부 처리 (403 Forbidden)
                ex.accessDeniedHandler { request, response, accessDeniedException ->
                    val logger = org.slf4j.LoggerFactory.getLogger("SecurityExceptionHandler")
                    logger.warn("Access denied: ${accessDeniedException.message} for ${request.requestURI}")

                    response.contentType = "application/json;charset=UTF-8"
                    response.status = HttpServletResponse.SC_FORBIDDEN

                    val errorJson = """
                        {
                            "timestamp": "${java.time.Instant.now()}",
                            "status": 403,
                            "error": "Forbidden",
                            "message": "Access denied: ${accessDeniedException.message}",
                            "path": "${request.requestURI}"
                        }
                    """.trimIndent()

                    response.writer.write(errorJson)
                }
            }
            .build()
    }

}
