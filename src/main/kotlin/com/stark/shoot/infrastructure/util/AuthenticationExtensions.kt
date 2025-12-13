package com.stark.shoot.infrastructure.util

import com.stark.shoot.domain.shared.UserId
import org.springframework.security.core.Authentication
import java.security.Principal

/**
 * Authentication 및 Principal 확장 함수
 * Spring Security Authentication 및 WebSocket Principal 객체에서 UserId를 추출하는 유틸리티
 */

/**
 * Authentication 객체에서 UserId를 추출합니다.
 *
 * @return UserId 값 객체
 * @throws NumberFormatException Authentication.name이 Long으로 변환 불가능한 경우
 */
fun Authentication.extractUserId(): UserId = UserId.from(name.toLong())

/**
 * Authentication 객체에서 사용자 ID를 Long 타입으로 추출합니다.
 *
 * @return 사용자 ID (Long)
 * @throws NumberFormatException Authentication.name이 Long으로 변환 불가능한 경우
 */
fun Authentication.extractUserIdAsLong(): Long = name.toLong()

/**
 * Principal 객체에서 UserId를 추출합니다.
 * WebSocket STOMP 연결에서 사용됩니다.
 *
 * @return UserId 값 객체
 * @throws NumberFormatException Principal.name이 Long으로 변환 불가능한 경우
 */
fun Principal.extractUserId(): UserId = UserId.from(name.toLong())

/**
 * Principal 객체에서 사용자 ID를 Long 타입으로 추출합니다.
 * WebSocket STOMP 연결에서 사용됩니다.
 *
 * @return 사용자 ID (Long)
 * @throws NumberFormatException Principal.name이 Long으로 변환 불가능한 경우
 */
fun Principal.extractUserIdAsLong(): Long = name.toLong()
