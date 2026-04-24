package com.stark.shoot.application.port.out.message

import com.stark.shoot.domain.chat.message.vo.MessageId
import com.stark.shoot.domain.shared.UserId

interface MessageCommandPort : SaveMessagePort {
    /**
     * 메시지 삭제 (보상 트랜잭션용)
     *
     * @param messageId 삭제할 메시지 ID
     */
    fun delete(messageId: MessageId)

    /**
     * 특정 사용자가 보낸 모든 메시지를 소프트 삭제합니다 (배치 작업)
     *
     * MongoDB의 updateMany를 사용하여 한 번의 쿼리로 처리합니다.
     * O(n) → O(1) 성능 개선
     *
     * @param senderId 발신자 사용자 ID
     * @return 업데이트된 메시지 수
     */
    fun markAllAsDeletedBySenderId(senderId: UserId): Long
}