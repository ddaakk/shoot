package com.stark.shoot.adapter.`in`.socket.message.thread

import com.stark.shoot.adapter.`in`.rest.dto.message.ChatMessageRequest
import com.stark.shoot.application.dto.message.MessageContentDto
import com.stark.shoot.application.dto.message.MessageMetadataDto
import com.stark.shoot.application.dto.message.SendMessageDto
import com.stark.shoot.application.port.`in`.message.thread.SendThreadMessageUseCase
import com.stark.shoot.application.port.`in`.message.thread.command.SendThreadMessageCommand
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
class ThreadMessageStompHandler(
    private val sendThreadMessageUseCase: SendThreadMessageUseCase,
) {

    /**
     * 스레드 메시지 전송 (WebSocket)
     *
     * WebSocket Endpoint: /thread
     * Protocol: STOMP over WebSocket
     */
    @MessageMapping("/thread")
    fun handleSendThreadMessage(request: ChatMessageRequest) {
        // Adapter DTO → Application DTO 변환
        val dto = toSendMessageDto(request)
        val command = SendThreadMessageCommand.of(dto)
        sendThreadMessageUseCase.sendThreadMessage(command)
    }

    /**
     * Adapter DTO를 Application DTO로 변환
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
