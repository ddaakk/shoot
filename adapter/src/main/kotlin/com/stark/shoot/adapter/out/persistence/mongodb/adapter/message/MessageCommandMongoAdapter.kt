package com.stark.shoot.adapter.out.persistence.mongodb.adapter.message

import com.mongodb.client.result.UpdateResult
import com.stark.shoot.adapter.out.persistence.mongodb.mapper.ChatMessageMapper
import com.stark.shoot.adapter.out.persistence.mongodb.repository.ChatMessageMongoRepository
import com.stark.shoot.application.port.out.message.MessageCommandPort
import com.stark.shoot.domain.chat.message.ChatMessage
import com.stark.shoot.domain.chat.message.vo.MessageId
import com.stark.shoot.domain.shared.UserId
import com.stark.shoot.infrastructure.annotation.Adapter
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update

@Adapter
class MessageCommandMongoAdapter(
    private val chatMessageRepository: ChatMessageMongoRepository,
    private val chatMessageMapper: ChatMessageMapper,
    private val mongoTemplate: MongoTemplate
) : MessageCommandPort {

    private val logger = KotlinLogging.logger {}

    /**
     * 채팅 메시지 저장
     *
     * @param message 채팅 메시지
     * @return 저장된 채팅 메시지
     */
    override fun save(
        message: ChatMessage
    ): ChatMessage {
        val document = chatMessageMapper.toDocument(message)
        return chatMessageRepository.save(document)
            .let(chatMessageMapper::toDomain)
    }

    /**
     * 채팅 메시지 목록 저장
     *
     * @param messages 채팅 메시지 목록
     * @return 저장된 채팅 메시지 목록
     */
    override fun saveAll(
        messages: List<ChatMessage>
    ): List<ChatMessage> {
        val documents = messages.map(chatMessageMapper::toDocument)
        return chatMessageRepository.saveAll(documents)
            .map(chatMessageMapper::toDomain)
            .toList()
    }

    /**
     * 채팅 메시지 삭제 (보상 트랜잭션용)
     *
     * Saga 실패 시 롤백을 위해 메시지를 물리적으로 삭제합니다.
     *
     * @param messageId 삭제할 메시지 ID
     */
    override fun delete(messageId: MessageId) {
        chatMessageRepository.deleteById(org.bson.types.ObjectId(messageId.value))
    }

    /**
     * 특정 사용자가 보낸 모든 메시지를 소프트 삭제합니다 (배치 작업)
     *
     * MongoDB의 updateMany를 사용하여 한 번의 쿼리로 처리합니다.
     * 기존 O(n) forEach 방식 대신 O(1) 배치 업데이트를 사용하여 성능 개선
     *
     * @param senderId 발신자 사용자 ID
     * @return 업데이트된 메시지 수
     */
    override fun markAllAsDeletedBySenderId(senderId: UserId): Long {
        return try {
            // 쿼리: senderId가 일치하고 아직 삭제되지 않은 메시지
            val query = Query().addCriteria(
                Criteria.where("senderId").`is`(senderId.value)
                    .and("isDeleted").ne(true)
            )

            // 업데이트: isDeleted = true로 설정
            val update = Update().set("isDeleted", true)

            // MongoDB updateMany 실행 (단일 쿼리로 모든 메시지 업데이트)
            val result: UpdateResult = mongoTemplate.updateMulti(query, update, "messages")

            val modifiedCount = result.modifiedCount
            logger.info {
                "사용자 메시지 배치 삭제 완료: senderId=${senderId.value}, " +
                "업데이트된 메시지 수=$modifiedCount"
            }

            modifiedCount
        } catch (e: Exception) {
            logger.error(e) {
                "사용자 메시지 배치 삭제 실패: senderId=${senderId.value}"
            }
            0L
        }
    }

}
