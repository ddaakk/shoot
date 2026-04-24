package com.stark.shoot.application.service.admin.outbox

import com.stark.shoot.application.port.`in`.admin.outbox.ResolveOutboxDeadLetterUseCase
import com.stark.shoot.application.port.`in`.admin.outbox.command.ResolveOutboxDeadLetterCommand
import com.stark.shoot.application.port.`in`.admin.outbox.result.OutboxDeadLetterResult
import com.stark.shoot.application.port.out.saga.OutboxDeadLetterPort
import com.stark.shoot.infrastructure.annotation.UseCase
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.transaction.annotation.Transactional

/**
 * Outbox Dead Letter 해결 서비스
 *
 * 관리자가 실패한 이벤트를 수동으로 해결 처리할 수 있는 서비스
 */
@Transactional
@UseCase
class ResolveOutboxDeadLetterService(
    private val deadLetterPort: OutboxDeadLetterPort
) : ResolveOutboxDeadLetterUseCase {

    private val logger = KotlinLogging.logger {}

    override fun resolveDLQ(command: ResolveOutboxDeadLetterCommand): OutboxDeadLetterResult {
        val dlq = deadLetterPort.findById(command.id)
            ?: throw IllegalArgumentException("DLQ를 찾을 수 없습니다: ${command.id}")

        if (dlq.resolved) {
            throw IllegalStateException("이미 해결된 DLQ입니다: ${command.id}")
        }

        dlq.markAsResolved(command.resolvedBy, command.note)
        val savedDLQ = deadLetterPort.save(dlq)

        logger.info { "DLQ 해결 처리: id=${command.id}, resolvedBy=${command.resolvedBy}" }

        return OutboxDeadLetterResult(
            id = savedDLQ.id,
            originalEventId = savedDLQ.originalEventId,
            sagaId = savedDLQ.sagaId,
            sagaState = savedDLQ.sagaState.name,
            eventType = savedDLQ.eventType,
            payload = savedDLQ.payload,
            failureReason = savedDLQ.failureReason,
            failureCount = savedDLQ.failureCount,
            lastFailureAt = savedDLQ.lastFailureAt,
            createdAt = savedDLQ.createdAt,
            resolved = savedDLQ.resolved,
            resolvedAt = savedDLQ.resolvedAt,
            resolvedBy = savedDLQ.resolvedBy,
            resolutionNote = savedDLQ.resolutionNote
        )
    }
}
