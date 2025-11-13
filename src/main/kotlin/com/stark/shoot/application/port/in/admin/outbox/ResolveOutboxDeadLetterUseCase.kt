package com.stark.shoot.application.port.`in`.admin.outbox

import com.stark.shoot.application.port.`in`.admin.outbox.command.ResolveOutboxDeadLetterCommand
import com.stark.shoot.application.port.`in`.admin.outbox.result.OutboxDeadLetterResult

/**
 * Outbox Dead Letter 해결 Use Case
 *
 * 관리자가 실패한 이벤트를 수동으로 해결 처리할 수 있는 Use Case
 */
interface ResolveOutboxDeadLetterUseCase {

    /**
     * DLQ를 해결 처리
     */
    fun resolveDLQ(command: ResolveOutboxDeadLetterCommand): OutboxDeadLetterResult
}
