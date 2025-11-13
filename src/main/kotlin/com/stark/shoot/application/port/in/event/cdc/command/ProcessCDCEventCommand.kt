package com.stark.shoot.application.port.`in`.event.cdc.command

/**
 * CDC 이벤트 처리 커맨드
 *
 * Debezium이 감지한 Outbox 테이블 변경사항을 처리하기 위한 커맨드
 *
 * @property debeziumMessage Debezium 전체 메시지 (JSON 형식)
 * @property topic Kafka 토픽
 * @property partition 파티션 번호
 * @property offset 오프셋
 */
data class ProcessCDCEventCommand(
    val debeziumMessage: String,
    val topic: String,
    val partition: Int,
    val offset: Long
) {
    companion object {
        fun of(
            debeziumMessage: String,
            topic: String,
            partition: Int,
            offset: Long
        ) = ProcessCDCEventCommand(
            debeziumMessage = debeziumMessage,
            topic = topic,
            partition = partition,
            offset = offset
        )
    }
}
