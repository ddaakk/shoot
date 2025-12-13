package com.stark.shoot.application.port.out.socket

import java.util.concurrent.CompletableFuture

/**
 * WebSocket 메시지 발신 포트
 *
 * 서버에서 클라이언트로 WebSocket 메시지를 전송하는 기능을 제공합니다.
 * 실패 시 재시도 및 캐싱 로직을 포함합니다.
 */
interface SendWebSocketMessagePort {

    /**
     * 특정 destination으로 메시지를 전송합니다.
     * 모든 구독자에게 브로드캐스트됩니다.
     *
     * @param destination WebSocket destination (예: /topic/chat/123)
     * @param payload 전송할 메시지 객체
     * @param retryCount 재시도 횟수 (기본: 3)
     * @return 전송 성공 여부를 담은 CompletableFuture
     */
    fun sendMessage(
        destination: String,
        payload: Any,
        retryCount: Int = 3
    ): CompletableFuture<Boolean>

    /**
     * 특정 사용자에게 개인 메시지를 전송합니다.
     * 사용자의 개인 큐로 전송됩니다.
     *
     * @param userId 사용자 ID
     * @param destination 사용자 큐 destination (예: /queue/notifications)
     * @param payload 전송할 메시지 객체
     * @param retryCount 재시도 횟수 (기본: 3)
     * @return 전송 성공 여부를 담은 CompletableFuture
     */
    fun sendToUser(
        userId: String,
        destination: String,
        payload: Any,
        retryCount: Int = 3
    ): CompletableFuture<Boolean>
}
