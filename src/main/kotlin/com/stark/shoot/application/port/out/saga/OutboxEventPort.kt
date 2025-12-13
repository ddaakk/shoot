package com.stark.shoot.application.port.out.saga

import com.stark.shoot.adapter.out.persistence.postgres.entity.OutboxEventEntity
import java.time.Instant

/**
 * Outbox 이벤트 영속화를 위한 Port
 *
 * Outbox Pattern을 통한 이벤트 발행을 위해 이벤트를 저장하고 조회합니다.
 */
interface OutboxEventPort {

    /**
     * 처리되지 않은 이벤트 조회 (생성 시간 순)
     *
     * @return 처리되지 않은 이벤트 목록
     */
    fun findUnprocessedEvents(): List<OutboxEventEntity>

    /**
     * 특정 Saga ID의 이벤트 조회
     *
     * @param sagaId Saga ID
     * @return 해당 Saga의 이벤트 목록 (생성 시간 순)
     */
    fun findEventsBySagaId(sagaId: String): List<OutboxEventEntity>

    /**
     * 멱등성 키 존재 여부 확인
     *
     * @param idempotencyKey 멱등성 키
     * @return 존재 여부
     */
    fun existsByIdempotencyKey(idempotencyKey: String): Boolean

    /**
     * Outbox 이벤트 저장
     *
     * @param event Outbox 이벤트 엔티티
     * @return 저장된 이벤트
     */
    fun saveEvent(event: OutboxEventEntity): OutboxEventEntity

    /**
     * Outbox 이벤트 목록 삭제
     *
     * @param events 삭제할 이벤트 목록
     */
    fun deleteEvents(events: List<OutboxEventEntity>)

    /**
     * 오래된 처리 완료 이벤트 조회
     *
     * @param threshold 기준 시간 (이 시간 이전에 처리된 이벤트)
     * @return 오래된 처리 완료 이벤트 목록
     */
    fun findOldProcessedEvents(threshold: Instant): List<OutboxEventEntity>

    /**
     * 재시도 횟수가 초과된 실패 이벤트 조회
     *
     * @param maxRetries 최대 재시도 횟수
     * @return 재시도 횟수 초과 이벤트 목록
     */
    fun findFailedEventsExceedingRetries(maxRetries: Int): List<OutboxEventEntity>
}
