package com.stark.shoot.application.port.`in`.message.thread

import com.stark.shoot.application.dto.message.response.ThreadSummaryDto
import com.stark.shoot.application.port.`in`.message.thread.command.GetThreadsCommand

interface GetThreadsUseCase {
    fun getThreads(command: GetThreadsCommand): List<ThreadSummaryDto>
}
