package com.stark.shoot.adapter.`in`.rest.dto.message

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.stark.shoot.domain.chat.message.type.MessageStatus
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChatMessageRequest(
    @JsonProperty("id") val id: String? = null,
    @JsonProperty("tempId") var tempId: String? = null,

    @field:NotNull(message = "채팅방 ID는 필수입니다")
    @field:Positive(message = "채팅방 ID는 양수여야 합니다")
    @JsonProperty("roomId") val roomId: Long,

    @field:NotNull(message = "발신자 ID는 필수입니다")
    @field:Positive(message = "발신자 ID는 양수여야 합니다")
    @JsonProperty("senderId") val senderId: Long,

    @field:NotNull(message = "메시지 내용은 필수입니다")
    @field:Valid
    @JsonProperty("content") val content: MessageContentRequest,

    @JsonProperty("threadId") val threadId: String? = null,
    @JsonProperty("status") var status: MessageStatus? = null,
    @JsonProperty("readBy") val readBy: Map<String, Boolean>? = null,
    @JsonProperty("metadata") var metadata: ChatMessageMetadataRequest = ChatMessageMetadataRequest()
)
