package com.stark.shoot.application.port.`in`.message.pin.result

import com.stark.shoot.domain.chat.message.ChatMessage
import com.stark.shoot.domain.chat.pin.MessagePin

/**
 * 고정된 메시지 조회 결과
 *
 * Use Case가 Controller에게 반환하는 결과 객체
 * 메시지와 메시지 고정 정보를 함께 포함
 *
 * @property messages 고정된 메시지 목록
 * @property messagePins 메시지 고정 정보 목록 (MessagePin Aggregate)
 */
data class PinnedMessagesResult(
    val messages: List<ChatMessage>,
    val messagePins: List<MessagePin>
)
