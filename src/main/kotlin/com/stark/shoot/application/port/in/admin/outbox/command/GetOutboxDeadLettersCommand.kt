package com.stark.shoot.application.port.`in`.admin.outbox.command

/**
 * 미해결 DLQ 목록 조회 커맨드
 *
 * @property page 페이지 번호 (0부터 시작)
 * @property size 페이지 크기
 */
data class GetOutboxDeadLettersCommand(
    val page: Int,
    val size: Int
) {
    companion object {
        fun of(page: Int, size: Int) = GetOutboxDeadLettersCommand(page, size)
    }
}
