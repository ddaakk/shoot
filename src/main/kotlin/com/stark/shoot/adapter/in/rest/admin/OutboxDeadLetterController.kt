package com.stark.shoot.adapter.`in`.rest.admin

import com.stark.shoot.application.port.`in`.admin.outbox.GetOutboxDeadLetterStatsUseCase
import com.stark.shoot.application.port.`in`.admin.outbox.GetOutboxDeadLettersUseCase
import com.stark.shoot.application.port.`in`.admin.outbox.ResolveOutboxDeadLetterUseCase
import com.stark.shoot.application.port.`in`.admin.outbox.command.GetOutboxDeadLetterCommand
import com.stark.shoot.application.port.`in`.admin.outbox.command.GetOutboxDeadLettersCommand
import com.stark.shoot.application.port.`in`.admin.outbox.command.ResolveOutboxDeadLetterCommand
import com.stark.shoot.application.port.`in`.admin.outbox.result.EventTypeStatsResult
import com.stark.shoot.application.port.`in`.admin.outbox.result.OutboxDeadLetterResult
import com.stark.shoot.application.port.`in`.admin.outbox.result.OutboxDeadLetterStatsResult
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

/**
 * Outbox Dead Letter Queue 관리 API
 *
 * **보안**: ADMIN 권한 필요
 * - 모든 엔드포인트는 ADMIN 역할을 가진 사용자만 접근 가능
 * - 운영자가 실패한 이벤트를 모니터링하고 수동으로 해결할 수 있는 인터페이스 제공
 */
@RestController
@RequestMapping("/api/admin/outbox-dlq")
@PreAuthorize("hasRole('ADMIN')")
class OutboxDeadLetterController(
    private val getOutboxDeadLettersUseCase: GetOutboxDeadLettersUseCase,
    private val resolveOutboxDeadLetterUseCase: ResolveOutboxDeadLetterUseCase,
    private val getOutboxDeadLetterStatsUseCase: GetOutboxDeadLetterStatsUseCase
) {

    @GetMapping
    fun getUnresolvedDLQ(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<Page<OutboxDeadLetterResult>> {
        val command = GetOutboxDeadLettersCommand.of(page, size)
        val dlqPage = getOutboxDeadLettersUseCase.getUnresolvedDLQ(command)
        return ResponseEntity.ok(dlqPage)
    }

    @GetMapping("/{id}")
    fun getDLQById(@PathVariable id: Long): ResponseEntity<OutboxDeadLetterResult> {
        val command = GetOutboxDeadLetterCommand.of(id)
        val dlq = getOutboxDeadLettersUseCase.getDLQById(command)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(dlq)
    }

    @GetMapping("/saga/{sagaId}")
    fun getDLQBySagaId(@PathVariable sagaId: String): ResponseEntity<List<OutboxDeadLetterResult>> {
        val dlqList = getOutboxDeadLettersUseCase.getDLQBySagaId(sagaId)
        return ResponseEntity.ok(dlqList)
    }

    @PostMapping("/{id}/resolve")
    fun resolveDLQ(@PathVariable id: Long, @RequestBody request: ResolveDLQRequest): ResponseEntity<OutboxDeadLetterResult> {
        val command = ResolveOutboxDeadLetterCommand.of(id, request.resolvedBy, request.note)
        return try {
            val resolvedDLQ = resolveOutboxDeadLetterUseCase.resolveDLQ(command)
            ResponseEntity.ok(resolvedDLQ)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        } catch (e: IllegalStateException) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/stats")
    fun getDLQStats(): ResponseEntity<DLQStatsResponse> {
        val stats = getOutboxDeadLetterStatsUseCase.getDLQStats()
        val response = DLQStatsResponse(
            unresolvedCount = stats.unresolvedCount,
            last24hCount = stats.last24hCount,
            failuresByType = stats.failuresByType.map {
                EventTypeStats(eventType = it.eventType, count = it.count)
            }
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping("/recent")
    fun getRecentDLQ(): ResponseEntity<List<OutboxDeadLetterResult>> {
        val recentDLQ = getOutboxDeadLettersUseCase.getRecentDLQ()
        return ResponseEntity.ok(recentDLQ)
    }
}

data class ResolveDLQRequest(val resolvedBy: String, val note: String? = null)
data class DLQStatsResponse(val unresolvedCount: Long, val last24hCount: Long, val failuresByType: List<EventTypeStats>)
data class EventTypeStats(val eventType: String, val count: Int)
