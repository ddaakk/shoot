package com.stark.shoot.application.port.out.message.reaction

import com.stark.shoot.domain.chat.message.vo.MessageId
import com.stark.shoot.domain.chat.reaction.MessageReaction
import com.stark.shoot.domain.chat.reaction.type.ReactionType
import com.stark.shoot.domain.chat.reaction.vo.MessageReactionId
import com.stark.shoot.domain.shared.UserId

/**
 * 메시지 리액션 조회 포트
 *
 * 리액션 조회를 담당합니다.
 */
interface MessageReactionQueryPort {

    /**
     * ID로 리액션 조회
     *
     * @param id 리액션 ID
     * @return 조회된 리액션 또는 null
     */
    fun findById(id: MessageReactionId): MessageReaction?

    /**
     * 메시지와 사용자로 리액션 조회
     *
     * @param messageId 메시지 ID
     * @param userId 사용자 ID
     * @return 조회된 리액션 또는 null
     */
    fun findByMessageIdAndUserId(messageId: MessageId, userId: UserId): MessageReaction?

    /**
     * 메시지의 모든 리액션 조회
     *
     * @param messageId 메시지 ID
     * @return 리액션 목록
     */
    fun findAllByMessageId(messageId: MessageId): List<MessageReaction>

    /**
     * 메시지의 특정 타입 리액션 조회
     *
     * @param messageId 메시지 ID
     * @param reactionType 리액션 타입
     * @return 리액션 목록
     */
    fun findAllByMessageIdAndReactionType(
        messageId: MessageId,
        reactionType: ReactionType
    ): List<MessageReaction>

    /**
     * 메시지의 리액션 개수 조회
     *
     * @param messageId 메시지 ID
     * @return 리액션 개수
     */
    fun countByMessageId(messageId: MessageId): Long

    /**
     * 메시지의 특정 타입 리액션 개수 조회
     *
     * @param messageId 메시지 ID
     * @param reactionType 리액션 타입
     * @return 리액션 개수
     */
    fun countByMessageIdAndReactionType(
        messageId: MessageId,
        reactionType: ReactionType
    ): Long

    /**
     * 메시지의 리액션 요약 조회
     * Map<ReactionType, Count>
     *
     * @param messageId 메시지 ID
     * @return 리액션 타입별 개수 맵
     */
    fun getReactionSummary(messageId: MessageId): Map<ReactionType, Long>

    /**
     * 여러 메시지의 리액션 요약을 배치로 조회
     * N+1 쿼리 문제를 방지하기 위한 배치 조회
     *
     * @param messageIds 메시지 ID 목록
     * @return 메시지 ID별 리액션 타입별 개수 맵
     */
    fun getReactionSummaryBatch(messageIds: List<MessageId>): Map<MessageId, Map<ReactionType, Long>>

    /**
     * 여러 메시지의 리액션을 배치로 조회하여 사용자 ID 목록 반환
     * N+1 쿼리 문제를 방지하기 위한 배치 조회
     *
     * @param messageIds 메시지 ID 목록
     * @return 메시지 ID별 리액션 타입 코드별 사용자 ID 집합
     */
    fun getReactionsWithUsersBatch(messageIds: List<MessageId>): Map<MessageId, Map<String, Set<Long>>>
}
