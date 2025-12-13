package com.stark.shoot.application.service.message.thread

import com.stark.shoot.application.port.`in`.message.thread.SendThreadMessageUseCase
import com.stark.shoot.application.port.`in`.message.thread.command.SendThreadMessageCommand
import com.stark.shoot.application.port.out.message.MessagePublisherPort
import com.stark.shoot.application.port.out.message.MessageQueryPort
import com.stark.shoot.application.port.out.message.preview.CacheUrlPreviewPort
import com.stark.shoot.application.port.out.message.preview.ExtractUrlPort
import com.stark.shoot.application.acl.*
import com.stark.shoot.domain.chat.message.service.MessageDomainService
import com.stark.shoot.domain.chat.message.vo.MessageId
import com.stark.shoot.domain.chatroom.vo.ChatRoomId
import com.stark.shoot.domain.shared.UserId
import com.stark.shoot.infrastructure.annotation.UseCase
import com.stark.shoot.infrastructure.exception.web.ResourceNotFoundException
import io.github.oshai.kotlinlogging.KotlinLogging

@UseCase
class SendThreadMessageService(
    private val messageQueryPort: MessageQueryPort,
    private val extractUrlPort: ExtractUrlPort,
    private val cacheUrlPreviewPort: CacheUrlPreviewPort,
    private val messagePublisherPort: MessagePublisherPort,
    private val messageDomainService: MessageDomainService
) : SendThreadMessageUseCase {

    private val logger = KotlinLogging.logger {}

    override fun sendThreadMessage(command: SendThreadMessageCommand) {
        val request = command.message
        val threadId = MessageId.from(
            request.threadId ?: throw IllegalArgumentException("threadId must not be null")
        )

        messageQueryPort.findById(threadId)
            ?: throw ResourceNotFoundException("스레드 루트 메시지를 찾을 수 없습니다: threadId=$threadId")

        runCatching {
            // 1. 도메인 객체 생성 및 비즈니스 로직 처리
            messageDomainService.createAndProcessMessage(
                roomId = ChatRoomId.from(request.roomId).toChat(),
                senderId = UserId.from(request.senderId),
                contentText = request.content.text,
                contentType = request.content.type,
                threadId = request.threadId.let { MessageId.from(it) },
                extractUrls = { text -> extractUrlPort.extractUrls(text) },
                getCachedPreview = { url -> cacheUrlPreviewPort.getCachedUrlPreview(url) }
            )
        }.onSuccess { domainMessage ->
            // 2. 메시지 발행 (Kafka)
            messagePublisherPort.publish(domainMessage)
        }.onFailure { throwable ->
            // 3. 실패 시 오류 처리
            logger.error(throwable) { "메시지 처리 중 예외 발생: ${throwable.message}" }
            // 기본 메시지 생성 후 에러 처리
            val errorMessage = messageDomainService.createAndProcessMessage(
                roomId = ChatRoomId.from(request.roomId).toChat(),
                senderId = UserId.from(request.senderId),
                contentText = request.content.text,
                contentType = request.content.type,
                threadId = request.threadId.let { MessageId.from(it) },
                extractUrls = { emptyList() },
                getCachedPreview = { null }
            )
            messagePublisherPort.handleProcessingError(errorMessage, throwable)
        }
    }
}