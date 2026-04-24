package com.stark.shoot.application.service.event.user

import com.stark.shoot.application.port.out.message.MessageCommandPort
import com.stark.shoot.application.port.out.message.MessageQueryPort
import com.stark.shoot.domain.shared.event.UserDeletedEvent
import com.stark.shoot.infrastructure.annotation.ApplicationEventListener
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async

/**
 * 사용자 삭제 시 MongoDB 데이터 클린업 리스너
 *
 * PostgreSQL ↔ MongoDB 데이터 일관성 유지
 *
 * 문제 상황:
 * - User가 PostgreSQL에서 삭제됨
 * - 하지만 MongoDB에는 해당 사용자가 보낸 메시지가 남아있음
 * - Orphaned documents 발생 (고아 문서)
 *
 * 해결 방법:
 * - UserDeletedEvent 수신 시 MongoDB 메시지 클린업
 * - 비동기 처리로 User 삭제 성능에 영향 없음
 * - 실패해도 로그만 남기고 User 삭제는 성공 (보상 가능)
 *
 * 성능 최적화:
 * - MongoDB updateMany를 사용한 배치 업데이트
 * - 기존 O(n) forEach 방식 대신 O(1) 단일 쿼리로 개선
 */
@ApplicationEventListener
class UserDeletedMongoCleanupListener(
    private val messageCommandPort: MessageCommandPort
) {

    private val logger = KotlinLogging.logger {}

    /**
     * 사용자 삭제 시 MongoDB 메시지 클린업 (배치 작업)
     *
     * 처리 내용:
     * 1. MongoDB updateMany를 사용하여 모든 메시지를 한 번에 업데이트
     * 2. 메시지 소프트 삭제 (isDeleted = true)
     * 3. 실패 시 로그만 남김 (User 삭제는 이미 완료됨)
     *
     * 성능 개선:
     * - 기존: forEach + N번의 save() → O(n) 쿼리
     * - 개선: MongoDB updateMany → O(1) 단일 쿼리
     * - 1000개 메시지: 1000번 쿼리 → 1번 쿼리
     *
     * @Async: 비동기 처리로 User 삭제 성능에 영향 없음
     */
    @Async
    @EventListener
    fun handleUserDeleted(event: UserDeletedEvent) {
        logger.info {
            "사용자 삭제 감지, MongoDB 메시지 클린업 시작: " +
            "userId=${event.userId.value}, username=${event.username}"
        }

        try {
            // MongoDB updateMany를 사용한 배치 업데이트 (O(1) 성능 개선)
            // 기존: forEach로 메시지 개별 업데이트 (O(n) 쿼리)
            // 개선: 단일 쿼리로 모든 메시지 업데이트 (O(1) 쿼리)
            val updatedCount = messageCommandPort.markAllAsDeletedBySenderId(event.userId)

            if (updatedCount == 0L) {
                logger.info { "클린업할 메시지 없음: userId=${event.userId.value}" }
                return
            }

            logger.info {
                "MongoDB 메시지 클린업 완료: userId=${event.userId.value}, " +
                "업데이트된 메시지 수=$updatedCount (배치 작업으로 성능 최적화됨)"
            }

        } catch (e: Exception) {
            // 클린업 실패해도 User 삭제는 이미 완료된 상태
            // 로그만 남기고 예외를 전파하지 않음
            logger.error(e) {
                "MongoDB 클린업 실패: userId=${event.userId.value}, " +
                "error=${e.message}"
            }

            // TODO: Dead Letter Queue에 추가하여 나중에 재시도
            // TODO: 관리자 대시보드에 실패 알림
        }
    }
}
