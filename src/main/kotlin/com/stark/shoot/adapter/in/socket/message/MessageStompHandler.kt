package com.stark.shoot.adapter.`in`.socket.message

import com.stark.shoot.adapter.`in`.rest.dto.message.ChatMessageRequest
import com.stark.shoot.application.dto.message.MessageContentDto
import com.stark.shoot.application.dto.message.MessageMetadataDto
import com.stark.shoot.application.dto.message.SendMessageDto
import com.stark.shoot.application.port.`in`.message.SendMessageUseCase
import com.stark.shoot.application.port.`in`.message.command.SendMessageCommand
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
class MessageStompHandler(
    private val sendMessageUseCase: SendMessageUseCase
) {

    /**
     * 클라이언트로부터 메시지를 수신하여 처리합니다.
     * 1. 메시지에 임시 ID와 "sending" 상태 추가
     * 2. Redis를 통해 메시지 즉시 브로드캐스트 (실시간성)
     * 3. Kafka를 통해 메시지 영속화 (안정성)
     * 4. 메시지 상태 업데이트를 클라이언트에 전송
     *
     * WebSocket Endpoint: /chat
     * Protocol: STOMP over WebSocket
     */
    @MessageMapping("/chat")
    fun handleChatMessage(message: ChatMessageRequest) {
        // Adapter DTO → Application DTO 변환
        val dto = toSendMessageDto(message)
        val command = SendMessageCommand.of(dto)
        sendMessageUseCase.sendMessage(command)
    }

    /**
     * Adapter DTO를 Application DTO로 변환
     *
     * Adapter 레이어와 Application 레이어의 경계에서 변환을 수행합니다.
     */
    private fun toSendMessageDto(request: ChatMessageRequest): SendMessageDto {
        return SendMessageDto(
            tempId = request.tempId,
            roomId = request.roomId,
            senderId = request.senderId,
            content = MessageContentDto(
                text = request.content.text,
                type = request.content.type,
                attachments = request.content.attachments
            ),
            threadId = request.threadId,
            metadata = MessageMetadataDto(
                needsUrlPreview = request.metadata.needsUrlPreview,
                previewUrl = request.metadata.previewUrl
            )
        )
    }

}
