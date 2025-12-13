package com.stark.shoot.application.mapper.message

import com.stark.shoot.application.dto.message.response.*
import com.stark.shoot.domain.chat.message.ChatMessage
import com.stark.shoot.domain.chat.message.vo.ChatMessageMetadata
import com.stark.shoot.domain.chat.message.vo.MessageContent
import org.springframework.stereotype.Component

/**
 * 메시지 도메인 객체를 Application DTO로 변환하는 Mapper
 *
 * Application 레이어에서 Domain → DTO 변환을 담당합니다.
 */
@Component
class MessageDtoMapper {

    /**
     * ChatMessage 도메인 객체를 MessageResponseDto로 변환
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
        )
    }

    /**
     * ChatMessage 리스트를 MessageResponseDto 리스트로 변환
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

    /**
     * 루트 메시지와 답글 개수로 ThreadSummaryDto 생성
     */
    fun toThreadSummaryDto(rootMessage: ChatMessage, replyCount: Long): ThreadSummaryDto {
        return ThreadSummaryDto(
            rootMessage = toDto(rootMessage),
            replyCount = replyCount
        )
    }

    /**
     * 루트 메시지와 답글 리스트로 ThreadDetailDto 생성
     */
    fun toThreadDetailDto(rootMessage: ChatMessage, messages: List<ChatMessage>): ThreadDetailDto {
        return ThreadDetailDto(
            rootMessage = toDto(rootMessage),
            messages = toDtoList(messages)
        )
    }
}
