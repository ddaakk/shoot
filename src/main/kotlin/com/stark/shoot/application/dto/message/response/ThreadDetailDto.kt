package com.stark.shoot.application.dto.message.response

/**
 * 스레드 상세 DTO (Application Layer)
 *
 * 스레드의 루트 메시지와 모든 답글을 포함합니다.
 */
data class ThreadDetailDto(
    val rootMessage: MessageResponseDto,
    val messages: List<MessageResponseDto>
)
