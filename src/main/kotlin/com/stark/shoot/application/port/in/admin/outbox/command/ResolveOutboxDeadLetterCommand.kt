package com.stark.shoot.application.port.`in`.admin.outbox.command

/**
 * DLQ 해결 커맨드
 *
 * @property id DLQ ID
 * @property resolvedBy 해결한 관리자 ID
 * @property note 해결 메모
 */
data class ResolveOutboxDeadLetterCommand(
    val id: Long,
    val resolvedBy: String,
    val note: String?
) {
    companion object {
        fun of(id: Long, resolvedBy: String, note: String? = null) =
            ResolveOutboxDeadLetterCommand(id, resolvedBy, note)
    }
}
