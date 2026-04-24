package com.stark.shoot.application.port.out.message

import com.stark.shoot.domain.chat.message.ChatMessage

/**
 * 메시지 발행을 위한 포트 인터페이스
 * 메시지를 발행하고 처리 오류를 처리하는 기능을 제공합니다.
 *
 * Application 레이어의 Port로서 Adapter DTO에 의존하지 않습니다.
 */
interface MessagePublisherPort {
    /**
     * 메시지를 발행합니다.
     *
     * @param domainMessage 도메인 메시지
     */
    fun publish(domainMessage: ChatMessage)

    /**
     * 메시지 처리 중 발생한 오류를 처리합니다.
     *
     * @param domainMessage 도메인 메시지
     * @param throwable 발생한 예외
     */
    fun handleProcessingError(domainMessage: ChatMessage, throwable: Throwable)
}