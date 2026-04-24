package com.stark.shoot.application.port.`in`.message.schedule.command

import com.stark.shoot.domain.shared.UserId
import org.springframework.security.core.Authentication
import com.stark.shoot.infrastructure.util.extractUserIdAsLong

/**
 * Command for canceling a scheduled message
 */
data class CancelScheduledMessageCommand(
    val scheduledMessageId: String,
    val userId: UserId
) {
    companion object {
        fun of(scheduledMessageId: String, userId: Long): CancelScheduledMessageCommand {
            return CancelScheduledMessageCommand(
                scheduledMessageId = scheduledMessageId,
                userId = UserId.from(userId)
            )
        }
        
        fun of(scheduledMessageId: String, authentication: Authentication): CancelScheduledMessageCommand {
            val userId = authentication.extractUserIdAsLong()
            return of(scheduledMessageId, userId)
        }
    }
}