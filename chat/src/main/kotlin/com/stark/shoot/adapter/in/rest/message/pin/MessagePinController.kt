package com.stark.shoot.adapter.`in`.rest.message.pin

import com.stark.shoot.adapter.`in`.rest.dto.ResponseDto
import com.stark.shoot.application.dto.message.pin.PinResponseDto
import com.stark.shoot.adapter.`in`.rest.dto.message.pin.PinResponse
import com.stark.shoot.application.port.`in`.message.pin.MessagePinUseCase
import com.stark.shoot.application.port.`in`.message.pin.command.PinMessageCommand
import com.stark.shoot.application.port.`in`.message.pin.command.UnpinMessageCommand
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@Tag(name = "메시지 고정", description = "메시지 고정 관련 API")
@RequestMapping("/api/v1/messages")
@RestController
class MessagePinController(
    private val messagePinUseCase: MessagePinUseCase
) {

    @Operation(
        summary = "메시지 고정",
        description = "중요한 메시지를 채팅방에 고정합니다. (이미 존재하면 해제하고 새로 고정)"
    )
    @PostMapping("/{messageId}/pin")
    fun pinMessage(
        @PathVariable messageId: String,
        authentication: Authentication
    ): ResponseDto<PinResponse> {
        val command = PinMessageCommand.of(messageId, authentication)
        val result = messagePinUseCase.pinMessage(command)

        return ResponseDto.success(
            result.toAdapterDto(),
            "메시지가 고정되었습니다."
        )
    }

    @Operation(
        summary = "메시지 고정 해제",
        description = "고정된 메시지를 해제합니다."
    )
    @DeleteMapping("/{messageId}/pin")
    fun unpinMessage(
        @PathVariable messageId: String,
        authentication: Authentication
    ): ResponseDto<PinResponse> {
        val command = UnpinMessageCommand.of(messageId, authentication)
        val result = messagePinUseCase.unpinMessage(command)

        return ResponseDto.success(
            result.toAdapterDto(),
            "메시지 고정이 해제되었습니다."
        )
    }

    // Application DTO → Adapter DTO 변환 확장 함수
    private fun PinResponseDto.toAdapterDto() = PinResponse(
        messageId = this.messageId,
        roomId = this.roomId,
        isPinned = this.isPinned,
        pinnedBy = this.pinnedBy,
        pinnedAt = this.pinnedAt,
        content = this.content,
        updatedAt = this.updatedAt
    )

}
