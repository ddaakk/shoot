package com.stark.shoot.application.dto.message.response

import com.stark.shoot.domain.chat.message.type.MessageStatus
import com.stark.shoot.domain.chat.message.type.MessageType
import java.time.Instant

/**
 * 메시지 응답 DTO (Application Layer)
 *
 * UseCase가 반환하는 메시지 데이터 객체
 */
data class MessageResponseDto(
    val id: String,
    val roomId: Long,
    val senderId: Long,
    val content: MessageContentResponseDto,
    val status: MessageStatus,
    val threadId: String? = null,
    val replyToMessageId: String? = null,
    val reactions: Map<String, Set<Long>> = emptyMap(),
    val mentions: Set<Long> = emptySet(),
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

/**
 * 메시지 컨텐츠 응답 DTO
 */
data class MessageContentResponseDto(
    val text: String,
    val type: MessageType,
    val attachments: List<AttachmentDto> = emptyList(),
    val isEdited: Boolean,
    val isDeleted: Boolean,
    val urlPreview: UrlPreviewDto? = null
)

/**
 * 첨부파일 DTO
 */
data class AttachmentDto(
    val id: String,
    val filename: String,
    val contentType: String,
    val size: Long,
    val url: String,
    val thumbnailUrl: String? = null
)

/**
 * URL 프리뷰 DTO
 */
data class UrlPreviewDto(
    val url: String,
    val title: String?,
    val description: String?,
    val imageUrl: String?,
    val siteName: String? = null
)
