package com.stark.shoot.adapter.out.persistence.mongodb.adapter.message

import com.stark.shoot.adapter.out.persistence.mongodb.mapper.MessageReactionMapper
import com.stark.shoot.adapter.out.persistence.mongodb.repository.MessageReactionRepository
import com.stark.shoot.application.port.out.message.reaction.MessageReactionCommandPort
import com.stark.shoot.application.port.out.message.reaction.MessageReactionQueryPort
import com.stark.shoot.domain.chat.message.vo.MessageId
import com.stark.shoot.domain.chat.reaction.MessageReaction
import com.stark.shoot.domain.chat.reaction.type.ReactionType
import com.stark.shoot.domain.chat.reaction.vo.MessageReactionId
import com.stark.shoot.domain.shared.UserId
import com.stark.shoot.infrastructure.annotation.Adapter
import io.github.oshai.kotlinlogging.KotlinLogging

/**
 * 메시지 리액션 영속성 Adapter
 *
 * MessageReactionCommandPort와 MessageReactionQueryPort를 구현하여
 * MongoDB와 Domain 계층을 연결합니다.
 */
@Adapter
class MessageReactionPersistenceAdapter(
    private val repository: MessageReactionRepository,
    private val mapper: MessageReactionMapper
) : MessageReactionCommandPort, MessageReactionQueryPort {

    private val logger = KotlinLogging.logger {}

    // ========== Command Port Implementation ==========

    override fun save(reaction: MessageReaction): MessageReaction {
        val document = mapper.toDocument(reaction)
        val saved = repository.save(document)
        logger.debug { "Saved message reaction: messageId=${reaction.messageId.value}, userId=${reaction.userId.value}, type=${reaction.reactionType}" }
        return mapper.toDomain(saved)
    }

    override fun delete(id: MessageReactionId) {
        repository.deleteById(id.value)
        logger.debug { "Deleted message reaction: id=${id.value}" }
    }

    override fun deleteByMessageIdAndUserId(messageId: MessageId, userId: UserId) {
        repository.deleteByMessageIdAndUserId(messageId.value, userId.value)
        logger.debug { "Deleted message reaction: messageId=${messageId.value}, userId=${userId.value}" }
    }

    override fun deleteAllByMessageId(messageId: MessageId) {
        repository.deleteAllByMessageId(messageId.value)
        logger.debug { "Deleted all message reactions: messageId=${messageId.value}" }
    }

    // ========== Query Port Implementation ==========

    override fun findById(id: MessageReactionId): MessageReaction? {
        return repository.findById(id.value)
            .map { mapper.toDomain(it) }
            .orElse(null)
    }

    override fun findByMessageIdAndUserId(messageId: MessageId, userId: UserId): MessageReaction? {
        return repository.findByMessageIdAndUserId(messageId.value, userId.value)
            ?.let { mapper.toDomain(it) }
    }

    override fun findAllByMessageId(messageId: MessageId): List<MessageReaction> {
        val documents = repository.findAllByMessageId(messageId.value)
        return mapper.toDomainList(documents)
    }

    override fun findAllByMessageIdAndReactionType(
        messageId: MessageId,
        reactionType: ReactionType
    ): List<MessageReaction> {
        val documents = repository.findAllByMessageIdAndReactionType(
            messageId.value,
            reactionType.name
        )
        return mapper.toDomainList(documents)
    }

    override fun countByMessageId(messageId: MessageId): Long {
        return repository.countByMessageId(messageId.value)
    }

    override fun countByMessageIdAndReactionType(
        messageId: MessageId,
        reactionType: ReactionType
    ): Long {
        return repository.countByMessageIdAndReactionType(
            messageId.value,
            reactionType.name
        )
    }

    override fun getReactionSummary(messageId: MessageId): Map<ReactionType, Long> {
        val reactions = repository.findAllByMessageId(messageId.value)

        // 타입별 개수 집계
        return reactions
            .groupBy { it.reactionType }
            .mapKeys { (typeStr, _) -> ReactionType.valueOf(typeStr) }
            .mapValues { (_, docs) -> docs.size.toLong() }
    }

    override fun getReactionSummaryBatch(messageIds: List<MessageId>): Map<MessageId, Map<ReactionType, Long>> {
        if (messageIds.isEmpty()) return emptyMap()

        // 모든 메시지의 리액션을 한 번의 쿼리로 조회
        val messageIdStrings = messageIds.map { it.value }
        val reactions = repository.findAllByMessageIdIn(messageIdStrings)

        // 메시지 ID별로 그룹화하고 각 메시지의 리액션 타입별 개수 집계
        return reactions
            .groupBy { it.messageId }
            .mapKeys { (messageIdStr, _) -> MessageId.from(messageIdStr) }
            .mapValues { (_, docs) ->
                docs
                    .groupBy { it.reactionType }
                    .mapKeys { (typeStr, _) -> ReactionType.valueOf(typeStr) }
                    .mapValues { (_, typeDocs) -> typeDocs.size.toLong() }
            }
    }

    override fun getReactionsWithUsersBatch(messageIds: List<MessageId>): Map<MessageId, Map<String, Set<Long>>> {
        if (messageIds.isEmpty()) return emptyMap()

        // 모든 메시지의 리액션을 한 번의 쿼리로 조회
        val messageIdStrings = messageIds.map { it.value }
        val reactions = repository.findAllByMessageIdIn(messageIdStrings)

        // 메시지 ID별로 그룹화하고 각 메시지의 리액션 타입별 사용자 ID 집합 생성
        return reactions
            .groupBy { it.messageId }
            .mapKeys { (messageIdStr, _) -> MessageId.from(messageIdStr) }
            .mapValues { (_, docs) ->
                docs
                    .groupBy { it.reactionType }
                    .mapValues { (_, typeDocs) ->
                        typeDocs.map { it.userId }.toSet()
                    }
            }
    }
}
