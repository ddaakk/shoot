package com.stark.shoot.infrastructure.config.kafka

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.kafka.config.TopicBuilder

/**
 * Kafka 토픽 자동 생성 설정
 *
 * 로컬/개발 환경에서만 자동으로 토픽을 생성합니다.
 * 프로덕션 환경에서는 인프라 팀이 IaC(Terraform, Helm 등)로 토픽을 수동 관리합니다.
 *
 * 환경별 동작:
 * - local/dev: 이 Config가 활성화되어 토픽 자동 생성
 * - prod: @Profile("!prod")로 비활성화 + spring.kafka.admin.auto-create=false
 */
@Configuration
@Profile("!prod")
class KafkaTopicConfig {

    /**
     * 채팅 메시지 토픽
     * 파티션 키: chatRoomId (채팅방별 순서 보장)
     */
    @Bean
    fun chatMessagesTopic(): NewTopic =
        TopicBuilder.name("chat-messages")
            .partitions(3)
            .replicas(2)
            .build()

    /**
     * 채팅 알림 토픽
     * 파티션 키: userId
     */
    @Bean
    fun chatNotificationsTopic(): NewTopic =
        TopicBuilder.name("chat-notifications")
            .partitions(3)
            .replicas(2)
            .build()

    /**
     * 채팅 이벤트 토픽
     * 파티션 키: chatRoomId
     */
    @Bean
    fun chatEventsTopic(): NewTopic =
        TopicBuilder.name("chat-events")
            .partitions(3)
            .replicas(2)
            .build()

    /**
     * Dead Letter Queue 토픽
     * 처리 실패한 메시지 저장 (7일 보관)
     */
    @Bean
    fun deadLetterTopic(): NewTopic =
        TopicBuilder.name("dead-letter-topic")
            .partitions(1)
            .replicas(2)
            .config("retention.ms", "604800000")
            .build()
}
