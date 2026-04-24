package com.stark.shoot.application.service.admin.outbox

import com.stark.shoot.adapter.out.persistence.postgres.entity.OutboxDeadLetterEntity
import com.stark.shoot.application.port.`in`.admin.outbox.GetOutboxDeadLettersUseCase
import com.stark.shoot.application.port.`in`.admin.outbox.command.GetOutboxDeadLetterCommand
import com.stark.shoot.application.port.`in`.admin.outbox.command.GetOutboxDeadLettersCommand
import com.stark.shoot.application.port.`in`.admin.outbox.result.OutboxDeadLetterResult
import com.stark.shoot.application.port.out.saga.OutboxDeadLetterPort
import com.stark.shoot.infrastructure.annotation.UseCase
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

/**
 * Outbox Dead Letter 조회 서비스
 *
 * 관리자가 실패한 이벤트를 조회할 수 있는 서비스
 */
@UseCase
class GetOutboxDeadLettersService(
    private val deadLetterPort: OutboxDeadLetterPort
) : GetOutboxDeadLettersUseCase {

    override fun getUnresolvedDLQ(command: GetOutboxDeadLettersCommand): Page<OutboxDeadLetterResult> {
        val pageable = PageRequest.of(
            command.page,
            command.size,
            Sort.by(Sort.Direction.DESC, "createdAt")
        )
        return deadLetterPort.findUnresolvedDLQ(pageable)
            .map { toResult(it) }
    }

    override fun getDLQById(command: GetOutboxDeadLetterCommand): OutboxDeadLetterResult? {
        return deadLetterPort.findById(command.id)?.let { toResult(it) }
    }

    override fun getDLQBySagaId(sagaId: String): List<OutboxDeadLetterResult> {
        return deadLetterPort.findBySagaId(sagaId)
            .map { toResult(it) }
    }

    override fun getRecentDLQ(): List<OutboxDeadLetterResult> {
        return deadLetterPort.findRecentUnresolvedDLQ()
            .map { toResult(it) }
    }

    /**
     * Entity를 Result로 변환
     */
    private fun toResult(entity: OutboxDeadLetterEntity): OutboxDeadLetterResult {
        return OutboxDeadLetterResult(
            id = entity.id,
            originalEventId = entity.originalEventId,
            sagaId = entity.sagaId,
            sagaState = entity.sagaState.name,
            eventType = entity.eventType,
            payload = entity.payload,
            failureReason = entity.failureReason,
            failureCount = entity.failureCount,
            lastFailureAt = entity.lastFailureAt,
            createdAt = entity.createdAt,
            resolved = entity.resolved,
            resolvedAt = entity.resolvedAt,
            resolvedBy = entity.resolvedBy,
            resolutionNote = entity.resolutionNote
        )
    }
}
