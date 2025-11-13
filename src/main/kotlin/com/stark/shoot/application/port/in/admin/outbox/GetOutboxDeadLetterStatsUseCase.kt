package com.stark.shoot.application.port.`in`.admin.outbox

import com.stark.shoot.application.port.`in`.admin.outbox.result.OutboxDeadLetterStatsResult

/**
 * Outbox Dead Letter 통계 조회 Use Case
 *
 * 관리자가 DLQ 통계를 조회할 수 있는 Use Case
 */
interface GetOutboxDeadLetterStatsUseCase {

    /**
     * DLQ 통계 조회
     */
    fun getDLQStats(): OutboxDeadLetterStatsResult
}
