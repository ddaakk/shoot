package com.stark.shoot.application.port.out.saga

import com.stark.shoot.adapter.out.persistence.postgres.entity.OutboxDeadLetterEntity
import com.stark.shoot.domain.saga.SagaState
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.Instant

/**
 * Outbox Dead Letter Queue를 위한 Port
 *
 * 실패한 Outbox 이벤트를 관리하기 위한 인터페이스입니다.
 */
interface OutboxDeadLetterPort {

    /**
     * 미해결 DLQ 전체 조회 (최신순)
     *
     * @return 미해결 DLQ 목록
     */
    fun findUnresolvedDLQ(): List<OutboxDeadLetterEntity>

    /**
     * 미해결 DLQ 페이징 조회
     *
     * @param pageable 페이징 정보
     * @return 미해결 DLQ 페이지
     */
    fun findUnresolvedDLQ(pageable: Pageable): Page<OutboxDeadLetterEntity>

    /**
     * 특정 Saga ID의 DLQ 조회
     *
     * @param sagaId Saga ID
     * @return 해당 Saga의 DLQ 목록
     */
    fun findBySagaId(sagaId: String): List<OutboxDeadLetterEntity>

    /**
     * 특정 이벤트 타입의 미해결 DLQ 조회
     *
     * @param eventType 이벤트 타입
     * @return 미해결 DLQ 목록
     */
    fun findByEventTypeAndUnresolved(eventType: String): List<OutboxDeadLetterEntity>

    /**
     * 특정 Saga 상태의 미해결 DLQ 조회
     *
     * @param sagaState Saga 상태
     * @return 미해결 DLQ 목록
     */
    fun findBySagaStateAndUnresolved(sagaState: SagaState): List<OutboxDeadLetterEntity>

    /**
     * 미해결 DLQ 개수
     *
     * @return 미해결 DLQ 개수
     */
    fun countUnresolved(): Long

    /**
     * 특정 기간 내 생성된 DLQ 개수
     *
     * @param since 기준 시간
     * @return DLQ 개수
     */
    fun countSince(since: Instant): Long

    /**
     * 오래된 해결된 DLQ 조회 (정리용)
     *
     * @param threshold 기준 시간 (이 시간 이전에 해결된 DLQ)
     * @return 오래된 해결된 DLQ 목록
     */
    fun findOldResolvedDLQ(threshold: Instant): List<OutboxDeadLetterEntity>

    /**
     * 이벤트 타입별 실패 통계
     *
     * @return 이벤트 타입별 실패 통계
     */
    fun getFailureStatsByEventType(): List<Map<String, Any>>

    /**
     * 최근 N개 미해결 DLQ 조회
     *
     * @return 최근 미해결 DLQ 목록 (최대 10개)
     */
    fun findRecentUnresolvedDLQ(): List<OutboxDeadLetterEntity>

    /**
     * 특정 원본 이벤트 ID의 DLQ 존재 여부
     *
     * @param originalEventId 원본 이벤트 ID
     * @return 존재 여부
     */
    fun existsByOriginalEventId(originalEventId: Long): Boolean

    /**
     * DLQ 저장
     *
     * @param dlq DLQ 엔티티
     * @return 저장된 DLQ
     */
    fun save(dlq: OutboxDeadLetterEntity): OutboxDeadLetterEntity

    /**
     * ID로 DLQ 조회
     *
     * @param id DLQ ID
     * @return DLQ 엔티티 (Optional)
     */
    fun findById(id: Long): OutboxDeadLetterEntity?

    /**
     * DLQ 목록 삭제
     *
     * @param dlqs 삭제할 DLQ 목록
     */
    fun deleteAll(dlqs: List<OutboxDeadLetterEntity>)
}
