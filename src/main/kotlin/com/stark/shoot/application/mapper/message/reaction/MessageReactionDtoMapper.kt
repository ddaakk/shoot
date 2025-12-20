package com.stark.shoot.application.mapper.message.reaction

import com.stark.shoot.application.dto.message.reaction.ReactionInfoDto
import com.stark.shoot.application.dto.message.reaction.ReactionResponseDto
import com.stark.shoot.domain.chat.reaction.type.ReactionType
import org.springframework.stereotype.Component

/**
 * MessageReaction 도메인 객체를 Application DTO로 변환하는 Mapper
 *
 * Application 레이어에서 Domain → DTO 변환을 담당합니다.
 */
@Component
class MessageReactionDtoMapper {

    /**
     * 리액션 맵을 ReactionResponseDto로 변환
     *
     * @param messageId 메시지 ID
     * @param reactions 리액션 맵 (리액션 타입 -> 사용자 ID 집합)
     * @param updatedAt 업데이트 시간
     * @return 리액션 응답 DTO
     */
    fun toReactionResponseDto(
        messageId: String,
        reactions: Map<String, Set<Long>>,
        updatedAt: String
    ): ReactionResponseDto {
        val reactionInfos = fromReactionsMap(reactions)
        return ReactionResponseDto(messageId, reactionInfos, updatedAt)
    }

    /**
     * 리액션 맵을 ReactionInfoDto 리스트로 변환
     *
     * @param reactions 리액션 맵 (리액션 타입 -> 사용자 ID 집합)
     * @return ReactionInfoDto 리스트
     */
    private fun fromReactionsMap(reactions: Map<String, Set<Long>>): List<ReactionInfoDto> {
        return reactions.map { (reactionType, userIds) ->
            val type = ReactionType.fromCode(reactionType) ?: ReactionType.LIKE // 기본값

            ReactionInfoDto(
                reactionType = type.code,
                emoji = type.emoji,
                description = type.description,
                userIds = userIds.toList(),
                count = userIds.size
            )
        }
    }
}
