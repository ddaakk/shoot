package com.stark.shoot.application.service.admin.outbox

import com.stark.shoot.adapter.out.persistence.postgres.repository.OutboxDeadLetterRepository
import com.stark.shoot.application.port.`in`.admin.outbox.GetOutboxDeadLetterStatsUseCase
import com.stark.shoot.application.port.`in`.admin.outbox.result.EventTypeStatsResult
import com.stark.shoot.application.port.`in`.admin.outbox.result.OutboxDeadLetterStatsResult
import com.stark.shoot.infrastructure.annotation.UseCase
import java.time.Instant

/**
 * Outbox Dead Letter 통계 조회 서비스
 *
 * 관리자가 DLQ 통계를 조회할 수 있는 서비스
 */
@UseCase
class GetOutboxDeadLetterStatsService(
    private val deadLetterRepository: OutboxDeadLetterRepository
) : GetOutboxDeadLetterStatsUseCase {

    override fun getDLQStats(): OutboxDeadLetterStatsResult {
        val unresolvedCount = deadLetterRepository.countByResolvedFalse()
        val last24hCount = deadLetterRepository.countDLQSince(Instant.now().minusSeconds(24 * 3600))
        val failuresByType = deadLetterRepository.getFailureStatsByEventType()

        return OutboxDeadLetterStatsResult(
            unresolvedCount = unresolvedCount,
            last24hCount = last24hCount,
            failuresByType = failuresByType.map {
                EventTypeStatsResult(
                    eventType = it["eventType"] as String,
                    count = (it["count"] as Long).toInt()
                )
            }
        )
    }
}
