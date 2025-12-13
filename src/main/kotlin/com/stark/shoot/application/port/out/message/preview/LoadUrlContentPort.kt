package com.stark.shoot.application.port.out.message.preview

import com.stark.shoot.domain.chat.message.vo.ChatMessageMetadata

interface LoadUrlContentPort {
    /**
     * URL 콘텐츠를 비동기로 가져옵니다.
     * I/O 스레드에서 실행되어 메인 스레드를 블로킹하지 않습니다.
     *
     * @param url 가져올 URL
     * @return URL 미리보기 정보 (실패 시 null)
     */
    suspend fun fetchUrlContent(url: String): ChatMessageMetadata.UrlPreview?
}