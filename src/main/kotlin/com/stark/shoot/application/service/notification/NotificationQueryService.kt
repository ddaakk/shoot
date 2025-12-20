package com.stark.shoot.application.service.notification

import com.stark.shoot.application.dto.notification.NotificationResponseDto
import com.stark.shoot.application.mapper.notification.NotificationDtoMapper
import com.stark.shoot.application.port.`in`.notification.NotificationQueryUseCase
import com.stark.shoot.application.port.`in`.notification.command.*
import com.stark.shoot.application.port.out.notification.NotificationQueryPort
import org.springframework.stereotype.Service

@Service
class NotificationQueryService(
    private val notificationQueryPort: NotificationQueryPort,
    private val notificationDtoMapper: NotificationDtoMapper
) : NotificationQueryUseCase {

    /**
     * 유저에 대한 알림을 가져옵니다.
     * 기본적으로 최신 20개의 알림을 조회합니다. (limit=20, offset=0)
     *
     * @param command 알림 조회 커맨드
     * @return 알림 목록
     */
    override fun getNotificationsForUser(command: GetNotificationsCommand): List<NotificationResponseDto> {
        val notifications = notificationQueryPort.loadNotificationsForUser(command.userId, command.limit, command.offset)
        return notificationDtoMapper.toDtoList(notifications)
    }

    /**
     * 유저에 대한 읽지 않은 알림을 가져옵니다.
     * 기본적으로 최신 20개의 읽지 않은 알림을 조회합니다. (limit=20, offset=0)
     *
     * @param command 읽지 않은 알림 조회 커맨드
     * @return 읽지 않은 알림 목록
     */
    override fun getUnreadNotificationsForUser(command: GetUnreadNotificationsCommand): List<NotificationResponseDto> {
        val notifications = notificationQueryPort.loadUnreadNotificationsForUser(command.userId, command.limit, command.offset)
        return notificationDtoMapper.toDtoList(notifications)
    }

    /**
     * 사용자의 알림을 타입별로 조회합니다.
     * 기본적으로 최신 20개의 알림을 조회합니다. (limit=20, offset=0)
     *
     * @param command 알림 타입별 조회 커맨드
     * @return 알림 목록
     */
    override fun getNotificationsByType(command: GetNotificationsByTypeCommand): List<NotificationResponseDto> {
        val notifications = notificationQueryPort.loadNotificationsByType(command.userId, command.type, command.limit, command.offset)
        return notificationDtoMapper.toDtoList(notifications)
    }

    /**
     * 사용자의 알림을 소스 타입별로 조회합니다.
     * 기본적으로 최신 20개의 알림을 조회합니다. (limit=20, offset=0)
     *
     * @param command 알림 소스별 조회 커맨드
     * @return 알림 목록
     */
    override fun getNotificationsBySource(command: GetNotificationsBySourceCommand): List<NotificationResponseDto> {
        val notifications = notificationQueryPort.loadNotificationsBySource(
            command.userId,
            command.sourceType,
            command.sourceId,
            command.limit,
            command.offset
        )
        return notificationDtoMapper.toDtoList(notifications)
    }

    /**
     * 사용자의 읽지 않은 알림 개수를 조회합니다.
     *
     * @param command 읽지 않은 알림 개수 조회 커맨드
     * @return 읽지 않은 알림 개수
     */
    override fun getUnreadNotificationCount(command: GetUnreadNotificationCountCommand): Int {
        return notificationQueryPort.countUnreadNotifications(command.userId)
    }

}
