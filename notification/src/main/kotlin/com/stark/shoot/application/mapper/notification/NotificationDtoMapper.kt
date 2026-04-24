package com.stark.shoot.application.mapper.notification

import com.stark.shoot.application.dto.notification.NotificationResponseDto
import com.stark.shoot.domain.notification.Notification
import org.springframework.stereotype.Component

/**
 * Notification 도메인 객체를 Application DTO로 변환하는 Mapper
 *
 * Application 레이어에서 Domain → DTO 변환을 담당합니다.
 */
@Component
class NotificationDtoMapper {

    /**
     * Notification을 NotificationResponseDto로 변환
     *
     * @param notification 알림 도메인 객체
     * @return 알림 응답 DTO
     */
    fun toDto(notification: Notification): NotificationResponseDto {
        return NotificationResponseDto(
            id = notification.id?.value,
            userId = notification.userId.value,
            title = notification.title.value,
            message = notification.message.value,
            type = notification.type.name,
            sourceId = notification.sourceId,
            sourceType = notification.sourceType.name,
            isRead = notification.isRead,
            createdAt = notification.createdAt,
            readAt = notification.readAt,
            metadata = notification.metadata
        )
    }

    /**
     * Notification 리스트를 NotificationResponseDto 리스트로 변환
     *
     * @param notifications 알림 도메인 객체 리스트
     * @return 알림 응답 DTO 리스트
     */
    fun toDtoList(notifications: List<Notification>): List<NotificationResponseDto> {
        return notifications.map { toDto(it) }
    }
}
