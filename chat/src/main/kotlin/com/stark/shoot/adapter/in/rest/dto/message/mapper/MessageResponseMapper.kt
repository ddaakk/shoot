package com.stark.shoot.adapter.`in`.rest.dto.message.mapper

import com.stark.shoot.adapter.`in`.rest.dto.message.AttachmentDto
import com.stark.shoot.adapter.`in`.rest.dto.message.MessageContentResponseDto
import com.stark.shoot.adapter.`in`.rest.dto.message.MessageResponseDto
import com.stark.shoot.adapter.`in`.rest.dto.message.UrlPreviewDto
import com.stark.shoot.domain.chat.message.ChatMessage
import com.stark.shoot.domain.chat.message.vo.ChatMessageMetadata
import com.stark.shoot.domain.chat.message.vo.MessageContent
import org.springframework.stereotype.Component

/**
 * 메시지 응답 DTO 매퍼
 *
 * Domain 모델(ChatMessage)을 REST API 응답 DTO로 변환합니다.
 * adapter/in 레이어에서 사용하는 매퍼입니다.
 */
@Component
class MessageResponseMapper {

    /**
     * 도메인 모델(ChatMessage)을 MessageResponseDto로 변환
     */
    fun toDto(message: ChatMessage): MessageResponseDto {
        return MessageResponseDto(
            id = message.id?.value ?: "",
            roomId = message.roomId.value,
            senderId = message.senderId.value,
            content = MessageContentResponseDto(
                text = message.content.text,
                type = message.content.type,
                attachments = message.content.attachments.map { toAttachmentDto(it) },
                isEdited = message.content.isEdited,
                isDeleted = message.content.isDeleted,
                urlPreview = message.content.metadata?.urlPreview?.let { toUrlPreviewDto(it) }
            ),
            status = message.status,
            threadId = message.threadId?.value,
            replyToMessageId = message.replyToMessageId?.value,
            reactions = emptyMap(),  // 리액션은 별도 Aggregate로 관리 (MessageReaction)
            mentions = message.mentions.map { it.value }.toSet(),
            createdAt = message.createdAt,
            updatedAt = message.updatedAt
            // 메시지 고정 정보는 별도 MessagePin Aggregate로 관리
            // 메시지 읽음 표시는 별도 MessageReadReceipt Aggregate로 관리
        )
    }

    /**
     * 도메인 모델 리스트를 DTO 리스트로 변환
     */
    fun toDtoList(messages: List<ChatMessage>): List<MessageResponseDto> {
        return messages.map { toDto(it) }
    }

    /**
     * 도메인 Attachment를 AttachmentDto로 변환
     */
    private fun toAttachmentDto(attachment: MessageContent.Attachment): AttachmentDto {
        return AttachmentDto(
            id = attachment.id,
            filename = attachment.filename,
            contentType = attachment.contentType,
            size = attachment.size,
            url = attachment.url,
            thumbnailUrl = attachment.thumbnailUrl
        )
    }

    /**
     * UrlPreview 도메인 객체를 DTO로 변환
     */
    private fun toUrlPreviewDto(preview: ChatMessageMetadata.UrlPreview): UrlPreviewDto {
        return UrlPreviewDto(
            url = preview.url,
            title = preview.title,
            description = preview.description,
            imageUrl = preview.imageUrl,
            siteName = preview.siteName
        )
    }
}
