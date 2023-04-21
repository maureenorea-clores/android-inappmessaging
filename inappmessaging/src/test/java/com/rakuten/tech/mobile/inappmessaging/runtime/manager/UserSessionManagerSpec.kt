package com.rakuten.tech.mobile.inappmessaging.runtime.manager

import com.nhaarman.mockitokotlin2.verify
import com.rakuten.tech.mobile.inappmessaging.runtime.data.repositories.AccountRepository
import com.rakuten.tech.mobile.inappmessaging.runtime.utils.EventMatchingUtil
import com.rakuten.tech.mobile.inappmessaging.runtime.workmanager.schedulers.MessageMixerPingScheduler
import org.junit.Test
import org.mockito.Mockito.mock

class UserSessionManagerSpec {
    private val manager = UserSessionManager(
        eventMatchingUtil = mock(EventMatchingUtil::class.java),
        messageReadinessManager = mock(MessageReadinessManager::class.java),
        accountRepo = mock(AccountRepository::class.java),
        pingScheduler = mock(MessageMixerPingScheduler::class.java),
    )

    @Test
    fun `should clear session data when calling onSessionUpdate()`() {
        manager.onSessionUpdate()

        verify(manager.eventMatchingUtil).clearNonPersistentEvents()
        verify(manager.messageReadinessManager).clearMessages()
        verify(manager.accountRepo).clearUserOldCacheStructure()
        verify(manager.pingScheduler).pingMessageMixerService(0)
    }
}

