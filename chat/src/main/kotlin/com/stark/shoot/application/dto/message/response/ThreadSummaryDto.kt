package com.stark.shoot.application.dto.message.response

/**
 * 스레드 요약 DTO (Application Layer)
 *
 * 스레드의 루트 메시지와 답글 개수를 포함합니다.
 */
data class ThreadSummaryDto(
    val rootMessage: MessageResponseDto,
    val replyCount: Long
)
