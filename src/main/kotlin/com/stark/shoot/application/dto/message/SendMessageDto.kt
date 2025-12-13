package com.stark.shoot.application.dto.message

import com.stark.shoot.domain.chat.message.type.MessageType

/**
 * Application 레이어의 메시지 전송 DTO
 *
 * Adapter 레이어의 REST/WebSocket DTO와 독립적입니다.
 * 비즈니스 로직 처리에 필요한 데이터만 포함합니다.
 */
data class SendMessageDto(
    val tempId: String?,
    val roomId: Long,
    val senderId: Long,
    val content: MessageContentDto,
    val threadId: String? = null,
    val metadata: MessageMetadataDto = MessageMetadataDto()
)

/**
 * 메시지 내용 DTO
 */
data class MessageContentDto(
    val text: String,
    val type: MessageType,
    val attachments: List<String> = emptyList()
)

/**
 * 메시지 메타데이터 DTO
 */
data class MessageMetadataDto(
    val needsUrlPreview: Boolean = false,
    val previewUrl: String? = null
)
