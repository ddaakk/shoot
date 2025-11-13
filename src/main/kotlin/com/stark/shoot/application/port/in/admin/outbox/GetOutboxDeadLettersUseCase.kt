package com.stark.shoot.application.port.`in`.admin.outbox

import com.stark.shoot.application.port.`in`.admin.outbox.command.GetOutboxDeadLetterCommand
import com.stark.shoot.application.port.`in`.admin.outbox.command.GetOutboxDeadLettersCommand
import com.stark.shoot.application.port.`in`.admin.outbox.result.OutboxDeadLetterResult
import org.springframework.data.domain.Page

/**
 * Outbox Dead Letter 조회 Use Case
 *
 * 관리자가 실패한 이벤트를 조회할 수 있는 Use Case
 */
interface GetOutboxDeadLettersUseCase {

    /**
     * 미해결 DLQ를 페이지네이션으로 조회
     */
    fun getUnresolvedDLQ(command: GetOutboxDeadLettersCommand): Page<OutboxDeadLetterResult>

    /**
     * ID로 특정 DLQ 조회
     */
    fun getDLQById(command: GetOutboxDeadLetterCommand): OutboxDeadLetterResult?

    /**
     * SagaId로 DLQ 목록 조회
     */
    fun getDLQBySagaId(sagaId: String): List<OutboxDeadLetterResult>

    /**
     * 최근 미해결 DLQ 10개 조회
     */
    fun getRecentDLQ(): List<OutboxDeadLetterResult>
}
