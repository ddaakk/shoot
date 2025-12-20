package com.stark.shoot.application.port.`in`.notification

import com.stark.shoot.application.dto.notification.NotificationResponseDto
import com.stark.shoot.application.port.`in`.notification.command.*

interface NotificationQueryUseCase {

    fun getNotificationsForUser(command: GetNotificationsCommand): List<NotificationResponseDto>
    fun getUnreadNotificationsForUser(command: GetUnreadNotificationsCommand): List<NotificationResponseDto>
    fun getNotificationsByType(command: GetNotificationsByTypeCommand): List<NotificationResponseDto>
    fun getNotificationsBySource(command: GetNotificationsBySourceCommand): List<NotificationResponseDto>
    fun getUnreadNotificationCount(command: GetUnreadNotificationCountCommand): Int

}
