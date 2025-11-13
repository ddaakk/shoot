package com.stark.shoot.application.port.`in`.admin.outbox.result

import java.time.Instant

/**
 * Outbox Dead Letter 결과
 *
 * Use Case가 Controller에게 반환하는 결과 객체
 */
data class OutboxDeadLetterResult(
    val id: Long?,
    val originalEventId: Long,
    val sagaId: String,
    val sagaState: String,
    val eventType: String,
    val payload: String,
    val failureReason: String,
    val failureCount: Int,
    val lastFailureAt: Instant,
    val createdAt: Instant,
    val resolved: Boolean,
    val resolvedAt: Instant?,
    val resolvedBy: String?,
    val resolutionNote: String?
)
