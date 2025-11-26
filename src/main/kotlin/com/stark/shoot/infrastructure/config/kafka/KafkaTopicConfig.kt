package com.stark.shoot.infrastructure.config.kafka

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

/**
 * Kafka 토픽 자동 생성 설정
 *
 * 로컬/개발 환경에서는 자동으로 토픽을 생성하고,
 * 프로덕션에서는 수동 관리를 권장합니다.
 *
 * 프로덕션에서 자동 생성 비활성화:
 * spring.kafka.admin.auto-create=false
 */
@Configuration
class KafkaTopicConfig {

    @Value("\${spring.kafka.topics.chat-messages.partitions:3}")
    private val chatMessagesPartitions: Int = 3

    @Value("\${spring.kafka.topics.chat-messages.replicas:2}")
    private val chatMessagesReplicas: Int = 2

    @Value("\${spring.kafka.topics.chat-notifications.partitions:3}")
    private val chatNotificationsPartitions: Int = 3

    @Value("\${spring.kafka.topics.chat-notifications.replicas:2}")
    private val chatNotificationsReplicas: Int = 2

    @Value("\${spring.kafka.topics.chat-events.partitions:3}")
    private val chatEventsPartitions: Int = 3

    @Value("\${spring.kafka.topics.chat-events.replicas:2}")
    private val chatEventsReplicas: Int = 2

    @Value("\${spring.kafka.topics.dead-letter-topic.partitions:1}")
    private val deadLetterPartitions: Int = 1

    @Value("\${spring.kafka.topics.dead-letter-topic.replicas:2}")
    private val deadLetterReplicas: Int = 2

    @Value("\${spring.kafka.topics.dead-letter-topic.retention-ms:604800000}")
    private val deadLetterRetentionMs: Long = 604800000 // 7일

    /**
     * 채팅 메시지 토픽
     * 파티션 키: chatRoomId (채팅방별 순서 보장)
     */
    @Bean
    fun chatMessagesTopic(): NewTopic =
        TopicBuilder.name("chat-messages")
            .partitions(chatMessagesPartitions)
            .replicas(chatMessagesReplicas.toShort().toInt())
            .build()

    /**
     * 채팅 알림 토픽
     * 파티션 키: userId
     */
    @Bean
    fun chatNotificationsTopic(): NewTopic =
        TopicBuilder.name("chat-notifications")
            .partitions(chatNotificationsPartitions)
            .replicas(chatNotificationsReplicas.toShort().toInt())
            .build()

    /**
     * 채팅 이벤트 토픽
     * 파티션 키: chatRoomId
     */
    @Bean
    fun chatEventsTopic(): NewTopic =
        TopicBuilder.name("chat-events")
            .partitions(chatEventsPartitions)
            .replicas(chatEventsReplicas.toShort().toInt())
            .build()

    /**
     * Dead Letter Queue 토픽
     * 처리 실패한 메시지 저장 (7일 보관)
     */
    @Bean
    fun deadLetterTopic(): NewTopic =
        TopicBuilder.name("dead-letter-topic")
            .partitions(deadLetterPartitions)
            .replicas(deadLetterReplicas.toShort().toInt())
            .config("retention.ms", deadLetterRetentionMs.toString())
            .build()
}
