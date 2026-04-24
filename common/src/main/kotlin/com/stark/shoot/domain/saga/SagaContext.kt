package com.stark.shoot.domain.saga

/**
 * SagaOrchestrator가 구체적인 Application 컨텍스트 타입에 의존하지 않도록 하는 공통 계약입니다.
 */
interface SagaContext {
    fun markCompensated()
    fun markFailed(throwable: Throwable)
}
