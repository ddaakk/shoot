package com.stark.shoot.adapter.out.cache.redis

import com.fasterxml.jackson.databind.ObjectMapper
import com.stark.shoot.application.port.out.message.FailedMessageCachePort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * Redis 기반 실패한 메시지 캐시 Adapter
 *
 * WebSocket 전송에 실패한 메시지를 Redis에 임시 저장합니다.
 * 나중에 재처리하거나 모니터링할 수 있습니다.
 */
@Component
class FailedMessageCacheAdapter(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper
) : FailedMessageCachePort {

    private val logger = KotlinLogging.logger {}

    override fun saveFailedMessage(key: String, payload: Any, ttlHours: Long) {
        try {
            val value = objectMapper.writeValueAsString(payload)
            redisTemplate.opsForValue().set(key, value, ttlHours, TimeUnit.HOURS)
            logger.warn { "실패한 메시지를 Redis에 저장: $key (TTL: ${ttlHours}시간)" }
        } catch (e: Exception) {
            logger.error(e) { "Redis에 실패한 메시지 저장 중 오류 발생: $key" }
        }
    }
}
