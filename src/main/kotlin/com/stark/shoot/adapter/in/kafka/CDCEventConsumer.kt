package com.stark.shoot.adapter.`in`.kafka

import com.stark.shoot.application.port.`in`.event.cdc.ProcessCDCEventUseCase
import com.stark.shoot.application.port.`in`.event.cdc.command.ProcessCDCEventCommand
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

/**
 * CDC 이벤트 소비자
 *
 * Debezium이 Outbox 테이블에서 감지한 변경사항을 Kafka에서 소비합니다.
 * - Topic: shoot.cdc.public.outbox_events (단일 토픽 방식)
 * - Debezium의 Simple CDC 구현 (EventRouter 없음)
 *
 * **동작 방식**:
 * 1. Debezium이 PostgreSQL WAL에서 Outbox 테이블 변경 감지
 * 2. 변경사항을 Debezium 표준 형식으로 Kafka 발행 (before/after/source 구조)
 * 3. 이 Consumer가 메시지에서 after 필드 추출
 * 4. event_type 기반으로 실제 비즈니스 이벤트를 내부 Kafka 토픽으로 재발행
 *
 * **OutboxEventProcessor와의 관계**:
 * - CDC가 정상: CDC가 실시간 발행 (<100ms)
 * - CDC 장애 시: OutboxEventProcessor가 폴링으로 백업 발행 (5초 주기)
 */
@Component
class CDCEventConsumer(
    private val processCDCEventUseCase: ProcessCDCEventUseCase
) {
    private val logger = KotlinLogging.logger {}

    /**
     * CDC 이벤트 소비 (Simple CDC - EventRouter 없음)
     *
     * Debezium 표준 형식:
     * - Topic: shoot.cdc.public.outbox_events
     * - Payload: { "before": null, "after": {...}, "source": {...}, "op": "c" }
     * - after 필드에 outbox_events 테이블 레코드 포함
     *
     * @param debeziumMessage Debezium 전체 메시지 (JSON)
     * @param topic Kafka 토픽
     * @param partition 파티션 번호
     * @param offset 오프셋
     */
    @KafkaListener(
        topics = ["shoot.cdc.public.outbox_events"],
        groupId = "shoot-cdc-consumer",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun consumeCDCEvent(
        @Payload debeziumMessage: String,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partition: Int,
        @Header(KafkaHeaders.OFFSET) offset: Long
    ) {
        val command = ProcessCDCEventCommand.of(
            debeziumMessage = debeziumMessage,
            topic = topic,
            partition = partition,
            offset = offset
        )

        processCDCEventUseCase.processCDCEvent(command)
    }
}
