package com.stark.shoot.application.port.out.message

/**
 * 실패한 메시지 캐시 Port
 *
 * WebSocket 전송에 실패한 메시지를 임시 저장하는 Outbound Port
 * Redis, In-Memory Cache 등 다양한 구현체로 교체 가능
 */
interface FailedMessageCachePort {

    /**
     * 실패한 메시지를 캐시에 저장
     *
     * @param key 캐시 키
     * @param payload 메시지 페이로드
     * @param ttlHours 캐시 유지 시간 (시간 단위)
     */
    fun saveFailedMessage(key: String, payload: Any, ttlHours: Long = 24)
}
