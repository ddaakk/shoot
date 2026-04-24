package com.stark.shoot.application.port.`in`.message.reaction

import com.stark.shoot.application.dto.message.reaction.ReactionResponseDto
import com.stark.shoot.application.port.`in`.message.reaction.command.ToggleMessageReactionCommand

interface ToggleMessageReactionUseCase {
    fun toggleReaction(command: ToggleMessageReactionCommand): ReactionResponseDto
}
