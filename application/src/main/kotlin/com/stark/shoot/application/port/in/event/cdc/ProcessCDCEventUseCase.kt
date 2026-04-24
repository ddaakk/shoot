package com.stark.shoot.application.port.`in`.event.cdc

import com.stark.shoot.application.port.`in`.event.cdc.command.ProcessCDCEventCommand

/**
 * CDC 이벤트 처리 Use Case
 *
 * Debezium이 감지한 Outbox 테이블 변경사항을 처리합니다.
 * - CDC 메시지 파싱
 * - 도메인 이벤트 역직렬화
 * - 비즈니스 이벤트 발행
 * - Outbox 테이블 업데이트
 */
interface ProcessCDCEventUseCase {

    /**
     * CDC 이벤트 처리
     *
     * @param command CDC 이벤트 처리 커맨드
     * @throws IllegalArgumentException CDC 메시지 형식이 잘못된 경우
     * @throws ClassNotFoundException 이벤트 클래스를 찾을 수 없는 경우
     */
    fun processCDCEvent(command: ProcessCDCEventCommand)
}
