package com.stark.shoot.adapter.`in`.rest.dto.message

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.stark.shoot.domain.chat.message.type.MessageType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@JsonIgnoreProperties(ignoreUnknown = true)
data class MessageContentRequest @JsonCreator constructor(
    @field:NotBlank(message = "메시지 내용은 필수입니다")
    @field:Size(max = 4000, message = "메시지는 최대 4000자까지 입력 가능합니다")
    @JsonProperty("text") val text: String,

    @field:NotNull(message = "메시지 타입은 필수입니다")
    @JsonProperty("type") val type: MessageType,

    @JsonProperty("attachments") val attachments: List<String> = emptyList(),
    @JsonProperty("isEdited") val isEdited: Boolean = false,
    @JsonProperty("isDeleted") val isDeleted: Boolean = false,
    @JsonProperty("urlPreview") var urlPreview: UrlPreviewDto? = null
)