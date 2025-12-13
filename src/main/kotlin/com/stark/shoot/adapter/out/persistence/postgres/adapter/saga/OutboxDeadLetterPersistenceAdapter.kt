package com.stark.shoot.adapter.out.persistence.postgres.adapter.saga

import com.stark.shoot.adapter.out.persistence.postgres.entity.OutboxDeadLetterEntity
import com.stark.shoot.adapter.out.persistence.postgres.repository.OutboxDeadLetterRepository
import com.stark.shoot.application.port.out.saga.OutboxDeadLetterPort
import com.stark.shoot.domain.saga.SagaState
import com.stark.shoot.infrastructure.annotation.Adapter
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.Instant

/**
 * Outbox Dead Letter 영속화 어댑터
 *
 * OutboxDeadLetterRepository를 감싸서 Port 인터페이스를 구현합니다.
 */
@Adapter
class OutboxDeadLetterPersistenceAdapter(
    private val deadLetterRepository: OutboxDeadLetterRepository
) : OutboxDeadLetterPort {

    override fun findUnresolvedDLQ(): List<OutboxDeadLetterEntity> =
        deadLetterRepository.findByResolvedFalseOrderByCreatedAtDesc()

    override fun findUnresolvedDLQ(pageable: Pageable): Page<OutboxDeadLetterEntity> =
        deadLetterRepository.findByResolvedFalse(pageable)

    override fun findBySagaId(sagaId: String): List<OutboxDeadLetterEntity> =
        deadLetterRepository.findBySagaIdOrderByCreatedAtDesc(sagaId)

    override fun findByEventTypeAndUnresolved(eventType: String): List<OutboxDeadLetterEntity> =
        deadLetterRepository.findByEventTypeAndResolvedFalse(eventType)

    override fun findBySagaStateAndUnresolved(sagaState: SagaState): List<OutboxDeadLetterEntity> =
        deadLetterRepository.findBySagaStateAndResolvedFalse(sagaState)

    override fun countUnresolved(): Long =
        deadLetterRepository.countByResolvedFalse()

    override fun countSince(since: Instant): Long =
        deadLetterRepository.countDLQSince(since)

    override fun findOldResolvedDLQ(threshold: Instant): List<OutboxDeadLetterEntity> =
        deadLetterRepository.findOldResolvedDLQ(threshold)

    override fun getFailureStatsByEventType(): List<Map<String, Any>> =
        deadLetterRepository.getFailureStatsByEventType()

    override fun findRecentUnresolvedDLQ(): List<OutboxDeadLetterEntity> =
        deadLetterRepository.findTop10ByResolvedFalseOrderByCreatedAtDesc()

    override fun existsByOriginalEventId(originalEventId: Long): Boolean =
        deadLetterRepository.existsByOriginalEventId(originalEventId)

    override fun save(dlq: OutboxDeadLetterEntity): OutboxDeadLetterEntity =
        deadLetterRepository.save(dlq)

    override fun findById(id: Long): OutboxDeadLetterEntity? =
        deadLetterRepository.findById(id).orElse(null)

    override fun deleteAll(dlqs: List<OutboxDeadLetterEntity>) =
        deadLetterRepository.deleteAll(dlqs)
}
