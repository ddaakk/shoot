package com.stark.shoot.application.service.event.cdc

import com.fasterxml.jackson.databind.ObjectMapper
import com.stark.shoot.application.port.`in`.event.cdc.ProcessCDCEventUseCase
import com.stark.shoot.application.port.`in`.event.cdc.command.ProcessCDCEventCommand
import com.stark.shoot.application.port.out.event.EventPublishPort
import com.stark.shoot.application.port.out.saga.OutboxEventPort
import com.stark.shoot.domain.shared.event.DomainEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * CDC 이벤트 처리 서비스
 *
 * Debezium이 감지한 Outbox 테이블 변경사항을 처리합니다.
 */
@Service
class ProcessCDCEventService(
    private val eventPublisher: EventPublishPort,
    private val outboxEventPort: OutboxEventPort,
    private val objectMapper: ObjectMapper
) : ProcessCDCEventUseCase {

    private val logger = KotlinLogging.logger {}

    @Transactional
    override fun processCDCEvent(command: ProcessCDCEventCommand) {
        logger.info {
            "CDC 이벤트 수신: topic=${command.topic}, partition=${command.partition}, offset=${command.offset}"
        }

        try {
            // 1. Debezium 메시지 파싱
            val debeziumPayload = objectMapper.readTree(command.debeziumMessage)
            val operation = debeziumPayload.get("op")?.asText()

            // INSERT, UPDATE만 처리 (DELETE는 무시)
            if (operation != "c" && operation != "u") {
                logger.debug { "CDC 이벤트 스킵 (op=$operation)" }
                return
            }

            val afterNode = debeziumPayload.get("after") ?: run {
                logger.warn { "CDC 메시지에 'after' 필드 없음" }
                return
            }

            // 2. Outbox 이벤트 정보 추출
            val sagaId = afterNode.get("saga_id")?.asText()
            val eventType = afterNode.get("event_type")?.asText()
            val payloadJson = afterNode.get("payload")?.asText()
            val processed = afterNode.get("processed")?.asBoolean() ?: false

            // 이미 처리된 이벤트는 스킵
            if (processed) {
                logger.debug { "이미 처리된 CDC 이벤트 스킵: sagaId=$sagaId" }
                return
            }

            if (eventType == null || payloadJson == null) {
                logger.warn { "CDC 메시지에 필수 필드 없음: eventType=$eventType, payload=$payloadJson" }
                return
            }

            // 3. 이벤트 역직렬화
            val eventClass = Class.forName(eventType)
            val event = objectMapper.readValue(payloadJson, eventClass) as DomainEvent

            // 4. 실제 비즈니스 이벤트 발행
            eventPublisher.publishEvent(event)

            logger.info {
                "CDC 이벤트 처리 완료: eventType=$eventType, sagaId=$sagaId"
            }

            // 5. Outbox 테이블 업데이트 (processed=true)
            if (sagaId != null) {
                markAsProcessedBySagaId(sagaId, eventType)
            }

        } catch (e: ClassNotFoundException) {
            logger.error(e) {
                "이벤트 클래스를 찾을 수 없음: message=${command.debeziumMessage.take(200)}"
            }
            // DLQ로 이동하거나 재시도 로직 필요
            throw e
        } catch (e: Exception) {
            logger.error(e) {
                "CDC 이벤트 처리 실패: topic=${command.topic}, message=${command.debeziumMessage.take(200)}"
            }
            // 예외 발생 시 Kafka가 자동으로 재시도
            throw e
        }
    }

    /**
     * Outbox 테이블에서 해당 이벤트를 처리 완료로 표시
     *
     * CDC가 처리한 이벤트는 OutboxEventProcessor가 다시 처리하지 않도록 함
     * 동일 Saga에 여러 이벤트가 있을 수 있으므로 event_type도 확인
     */
    private fun markAsProcessedBySagaId(sagaId: String, eventType: String) {
        try {
            val events = outboxEventPort.findEventsBySagaId(sagaId)

            events
                .filter { it.eventType == eventType && !it.processed }
                .forEach { event ->
                    event.markAsProcessed()
                    outboxEventPort.saveEvent(event)
                    logger.debug {
                        "Outbox 이벤트 처리 완료 표시: id=${event.id}, sagaId=$sagaId"
                    }
                }
        } catch (e: Exception) {
            // Outbox 업데이트 실패는 로그만 남기고 무시
            // 중요한 것은 이벤트 발행이므로, 업데이트 실패해도 계속 진행
            logger.warn(e) {
                "Outbox 업데이트 실패 (무시됨): sagaId=$sagaId, eventType=$eventType"
            }
        }
    }
}
