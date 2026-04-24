package com.stark.shoot.application.service.message

import com.stark.shoot.application.dto.message.SendMessageDto
import com.stark.shoot.application.port.`in`.message.SendMessageUseCase
import com.stark.shoot.application.port.`in`.message.command.SendMessageCommand
import com.stark.shoot.application.port.out.chatroom.ChatRoomQueryPort
import com.stark.shoot.application.port.out.message.MessagePublisherPort
import com.stark.shoot.application.port.out.message.preview.CacheUrlPreviewPort
import com.stark.shoot.application.port.out.message.preview.ExtractUrlPort
import com.stark.shoot.application.acl.*
import com.stark.shoot.domain.chat.message.ChatMessage
import com.stark.shoot.domain.chat.message.service.MessageDomainService
import com.stark.shoot.domain.chat.message.vo.MessageId
import com.stark.shoot.domain.chatroom.vo.ChatRoomId
import com.stark.shoot.domain.shared.UserId
import com.stark.shoot.infrastructure.annotation.UseCase
import com.stark.shoot.infrastructure.exception.web.UnauthorizedException
import io.github.oshai.kotlinlogging.KotlinLogging


@UseCase
class SendMessageService(
    private val chatRoomQueryPort: ChatRoomQueryPort,
    private val extractUrlPort: ExtractUrlPort,
    private val cacheUrlPreviewPort: CacheUrlPreviewPort,
    private val messagePublisherPort: MessagePublisherPort,
    private val messageDomainService: MessageDomainService
) : SendMessageUseCase {

    private val logger = KotlinLogging.logger {}

    /**
     * 메시지를 전송합니다.
     * 1. 도메인 객체 생성 및 비즈니스 로직 처리
     * 2. 메시지 발행 (Redis, Kafka)
     *
     * @param command 메시지 전송 커맨드
     * @see com.stark.shoot.adapter.in.redis.MessageRedisStreamListener redis 스트림 리스너
     * @see HandleMessageEventService Kafka 메시지 처리 서비스
     */
    override fun sendMessage(command: SendMessageCommand) {
        val dto = command.message

        // 권한 검증: 채팅방 참여자만 메시지를 전송할 수 있음
        val chatRoomId = ChatRoomId.from(dto.roomId)
        val chatRoom = chatRoomQueryPort.findById(chatRoomId)
            ?: throw IllegalStateException("채팅방을 찾을 수 없습니다: ${dto.roomId}")

        val senderId = UserId.from(dto.senderId)
        if (senderId !in chatRoom.participants) {
            throw UnauthorizedException("채팅방 참여자만 메시지를 전송할 수 있습니다.")
        }

        runCatching {
            // 1. 도메인 객체 생성 및 비즈니스 로직 처리
            createAndProcessDomainMessage(dto)
        }.onSuccess { domainMessage ->
            // 2. 메시지 발행 (Kafka)
            messagePublisherPort.publish(domainMessage)
        }.onFailure { throwable ->
            // 3. 실패 시 오류 처리
            logger.error(throwable) { "메시지 처리 중 예외 발생: ${throwable.message}" }
            // 도메인 메시지를 생성하지 못했으면 기본 메시지로 처리
            val errorMessage = createErrorMessage(dto)
            messagePublisherPort.handleProcessingError(errorMessage, throwable)
        }
    }

    /**
     * 도메인 메시지 객체를 생성하고 비즈니스 로직을 처리합니다.
     *
     * @param dto 메시지 전송 DTO
     * @return 처리된 도메인 메시지 객체
     */
    private fun createAndProcessDomainMessage(dto: SendMessageDto): ChatMessage {
        return messageDomainService.createAndProcessMessage(
            roomId = ChatRoomId.from(dto.roomId).toChat(),
            senderId = UserId.from(dto.senderId),
            contentText = dto.content.text,
            contentType = dto.content.type,
            tempId = dto.tempId,
            threadId = dto.threadId?.let { MessageId.from(it) },
            extractUrls = { text -> extractUrlPort.extractUrls(text) },
            getCachedPreview = { url -> cacheUrlPreviewPort.getCachedUrlPreview(url) }
        )
    }

    /**
     * 오류 발생 시 사용할 기본 도메인 메시지를 생성합니다.
     *
     * @param dto 메시지 전송 DTO
     * @return 기본 도메인 메시지
     */
    private fun createErrorMessage(dto: SendMessageDto): ChatMessage {
        // URL 처리 없이 기본 메시지만 생성
        return messageDomainService.createAndProcessMessage(
            roomId = ChatRoomId.from(dto.roomId).toChat(),
            senderId = UserId.from(dto.senderId),
            contentText = dto.content.text,
            contentType = dto.content.type,
            tempId = dto.tempId,
            threadId = dto.threadId?.let { MessageId.from(it) },
            extractUrls = { emptyList() }, // URL 처리 스킵
            getCachedPreview = { null } // 캐시 조회 스킵
        )
    }

}
