package com.stark.shoot.domain.saga

interface SagaContext {
    fun markCompensated()

    fun markFailed(throwable: Throwable)
}
