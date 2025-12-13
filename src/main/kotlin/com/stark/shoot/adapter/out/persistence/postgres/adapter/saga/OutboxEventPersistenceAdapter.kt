package com.stark.shoot.adapter.out.persistence.postgres.adapter.saga

import com.stark.shoot.adapter.out.persistence.postgres.entity.OutboxEventEntity
import com.stark.shoot.adapter.out.persistence.postgres.repository.OutboxEventRepository
import com.stark.shoot.application.port.out.saga.OutboxEventPort
import com.stark.shoot.infrastructure.annotation.Adapter
import java.time.Instant

/**
 * Outbox 이벤트 영속화 어댑터
 *
 * OutboxEventRepository를 감싸서 Port 인터페이스를 구현합니다.
 */
@Adapter
class OutboxEventPersistenceAdapter(
    private val outboxEventRepository: OutboxEventRepository
) : OutboxEventPort {

    override fun findUnprocessedEvents(): List<OutboxEventEntity> =
        outboxEventRepository.findByProcessedFalseOrderByCreatedAtAsc()

    override fun findEventsBySagaId(sagaId: String): List<OutboxEventEntity> =
        outboxEventRepository.findBySagaIdOrderByCreatedAtAsc(sagaId)

    override fun existsByIdempotencyKey(idempotencyKey: String): Boolean =
        outboxEventRepository.existsByIdempotencyKey(idempotencyKey)

    override fun saveEvent(event: OutboxEventEntity): OutboxEventEntity =
        outboxEventRepository.save(event)

    override fun deleteEvents(events: List<OutboxEventEntity>) =
        outboxEventRepository.deleteAll(events)

    override fun findOldProcessedEvents(threshold: Instant): List<OutboxEventEntity> =
        outboxEventRepository.findOldProcessedEvents(threshold)

    override fun findFailedEventsExceedingRetries(maxRetries: Int): List<OutboxEventEntity> =
        outboxEventRepository.findFailedEventsExceedingRetries(maxRetries)
}
