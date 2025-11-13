package com.stark.shoot.application.port.`in`.admin.outbox.result

/**
 * Outbox Dead Letter 통계 결과
 *
 * @property unresolvedCount 미해결 DLQ 개수
 * @property last24hCount 최근 24시간 DLQ 개수
 * @property failuresByType 이벤트 타입별 실패 통계
 */
data class OutboxDeadLetterStatsResult(
    val unresolvedCount: Long,
    val last24hCount: Long,
    val failuresByType: List<EventTypeStatsResult>
)

/**
 * 이벤트 타입별 통계
 */
data class EventTypeStatsResult(
    val eventType: String,
    val count: Int
)
