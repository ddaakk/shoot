package com.stark.shoot.application.dto.notification

import java.time.Instant

/**
 * 알림 응답 DTO (Application Layer)
 */
data class NotificationResponseDto(
    val id: String?,
    val userId: Long,
    val title: String,
    val message: String,
    val type: String,
    val sourceId: String,
    val sourceType: String,
    val isRead: Boolean,
    val createdAt: Instant,
    val readAt: Instant?,
    val metadata: Map<String, Any>
)
