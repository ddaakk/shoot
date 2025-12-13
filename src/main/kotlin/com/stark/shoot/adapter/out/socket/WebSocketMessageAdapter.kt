package com.stark.shoot.adapter.out.socket

import com.stark.shoot.adapter.`in`.rest.dto.message.MessageStatusResponse
import com.stark.shoot.application.port.out.message.FailedMessageCachePort
import com.stark.shoot.application.port.out.socket.SendWebSocketMessagePort
import com.stark.shoot.infrastructure.annotation.Adapter
import com.stark.shoot.infrastructure.config.async.ApplicationCoroutineScope
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import org.springframework.messaging.simp.SimpMessagingTemplate
import java.util.concurrent.CompletableFuture

/**
 * WebSocket 메시지 발신 Adapter
 *
 * SendWebSocketMessagePort를 구현하여 서버에서 클라이언트로
 * WebSocket 메시지를 전송합니다.
 *
 * 기능:
 * - 재시도 로직 (지수 백오프)
 * - 타임아웃 처리
 * - 실패 시 Redis 캐싱
 */
@Adapter
class WebSocketMessageAdapter(
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val failedMessageCachePort: FailedMessageCachePort,
    private val coroutineScope: ApplicationCoroutineScope
) : SendWebSocketMessagePort {

    private val logger = KotlinLogging.logger {}
    private val MESSAGE_SEND_TIMEOUT = 5000L

    @PreDestroy
    fun onApplicationShutdown() {
        logger.info { "애플리케이션 종료: WebSocket 메시지 어댑터 리소스 정리 중..." }
        shutdown()
    }

    /**
     * WebSocket을 통해 메시지를 전송합니다.
     * 실패 시 재시도 후 Redis에 저장합니다.
     */
    override fun sendMessage(destination: String, payload: Any, retryCount: Int): CompletableFuture<Boolean> {
        val result = CompletableFuture<Boolean>()

        coroutineScope.launch {
            var attempt = 0
            var success = false

            while (!success && attempt < retryCount) {
                try {
                    withTimeout(MESSAGE_SEND_TIMEOUT) {
                        simpMessagingTemplate.convertAndSend(destination, payload)
                    }
                    success = true
                } catch (e: TimeoutCancellationException) {
                    attempt++
                    logger.error(e) { "WebSocket 메시지 전송 타임아웃: $destination, 시도: $attempt/$retryCount" }
                    if (attempt < retryCount) {
                        delay(1000L * attempt) // 지수 백오프
                    }
                } catch (e: org.springframework.messaging.MessagingException) {
                    attempt++
                    logger.error(e) { "WebSocket 메시징 오류: $destination, 시도: $attempt/$retryCount" }
                    if (attempt < retryCount) {
                        delay(1000L * attempt)
                    }
                } catch (e: Exception) {
                    attempt++
                    logger.error(e) { "예상치 못한 전송 오류: $destination, 시도: $attempt/$retryCount" }
                    if (attempt < retryCount) {
                        delay(1000L * attempt)
                    }
                }
            }

            if (!success) {
                val key = when (payload) {
                    is MessageStatusResponse -> "failed-message-tempId:${payload.tempId}"
                    else -> "failed-message:${System.currentTimeMillis()}"
                }
                failedMessageCachePort.saveFailedMessage(key, payload)
                logger.warn { "WebSocket 전송 실패로 캐시에 저장: $key" }
            }

            result.complete(success)
        }.invokeOnCompletion { throwable ->
            throwable?.let {
                logger.error(it) { "코루틴 처리중 예외 발생" }
                if (!result.isDone) result.complete(false)
            }
        }

        return result
    }

    /**
     * 특정 사용자에게 메시지 전송
     * 재시도 로직과 지수 백오프 적용
     */
    override fun sendToUser(
        userId: String,
        destination: String,
        payload: Any,
        retryCount: Int
    ): CompletableFuture<Boolean> {
        val result = CompletableFuture<Boolean>()

        coroutineScope.launch {
            var attempt = 0
            var success = false

            while (!success && attempt < retryCount) {
                try {
                    withTimeout(MESSAGE_SEND_TIMEOUT) {
                        simpMessagingTemplate.convertAndSendToUser(userId, destination, payload)
                        logger.debug {
                            "사용자에게 메시지 전송 성공: userId=$userId, destination=$destination, attempt=${attempt + 1}"
                        }
                    }
                    success = true
                } catch (e: TimeoutCancellationException) {
                    attempt++
                    logger.warn {
                        "사용자 메시지 전송 시간 초과: userId=$userId, destination=$destination, attempt=$attempt/$retryCount"
                    }
                    if (attempt < retryCount) {
                        delay(1000L * attempt) // 지수 백오프
                    }
                } catch (e: org.springframework.messaging.MessagingException) {
                    attempt++
                    logger.error(e) {
                        "WebSocket 메시징 오류: userId=$userId, destination=$destination, attempt=$attempt/$retryCount"
                    }
                    if (attempt < retryCount) {
                        delay(1000L * attempt)
                    }
                } catch (e: Exception) {
                    attempt++
                    logger.error(e) {
                        "예상치 못한 전송 오류: userId=$userId, destination=$destination, attempt=$attempt/$retryCount"
                    }
                    if (attempt < retryCount) {
                        delay(1000L * attempt) // 지수 백오프
                    }
                }
            }

            result.complete(success)

            if (success) {
                logger.debug { "사용자에게 메시지 전송 완료: userId=$userId, destination=$destination" }
            } else {
                logger.error {
                    "사용자 메시지 전송 최종 실패: userId=$userId, destination=$destination, 총 시도 횟수=$attempt"
                }
                // 실패한 메시지를 캐시에 저장
                val key = "failed-user-message:$userId:${System.currentTimeMillis()}"
                failedMessageCachePort.saveFailedMessage(key, payload)
                logger.warn { "사용자 메시지 전송 실패로 캐시에 저장: $key" }
            }
        }

        return result
    }

    private fun shutdown() {
        logger.info { "WebSocket 메시지 어댑터 종료" }
    }
}
