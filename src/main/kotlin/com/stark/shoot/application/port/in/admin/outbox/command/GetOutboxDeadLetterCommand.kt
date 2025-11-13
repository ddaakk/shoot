package com.stark.shoot.application.port.`in`.admin.outbox.command

/**
 * 특정 DLQ 조회 커맨드
 *
 * @property id DLQ ID
 */
data class GetOutboxDeadLetterCommand(
    val id: Long
) {
    companion object {
        fun of(id: Long) = GetOutboxDeadLetterCommand(id)
    }
}
