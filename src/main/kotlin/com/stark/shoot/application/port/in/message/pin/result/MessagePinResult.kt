package com.stark.shoot.application.port.`in`.message.pin.result

import com.stark.shoot.domain.chat.message.ChatMessage
import com.stark.shoot.domain.chat.pin.MessagePin

/**
 * 메시지 고정/고정 해제 결과
 *
 * Use Case가 Controller에게 반환하는 결과 객체
 * 메시지와 메시지 고정 정보를 함께 포함
 *
 * @property message 대상 메시지
 * @property messagePin 메시지 고정 정보 (고정 해제 시 null)
 */
data class MessagePinResult(
    val message: ChatMessage,
    val messagePin: MessagePin?
)
