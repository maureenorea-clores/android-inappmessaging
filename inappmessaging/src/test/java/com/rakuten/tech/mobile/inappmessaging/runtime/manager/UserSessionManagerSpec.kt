package com.rakuten.tech.mobile.inappmessaging.runtime.manager

import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.rakuten.tech.mobile.inappmessaging.runtime.BuildConfig
import com.rakuten.tech.mobile.inappmessaging.runtime.InAppMessaging
import com.rakuten.tech.mobile.inappmessaging.runtime.data.repositories.AccountRepository
import com.rakuten.tech.mobile.inappmessaging.runtime.data.repositories.CampaignRepository
import com.rakuten.tech.mobile.inappmessaging.runtime.utils.EventMatchingUtil
import com.rakuten.tech.mobile.inappmessaging.runtime.workmanager.schedulers.MessageMixerPingScheduler
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner
import org.robolectric.util.ReflectionHelpers

/**
 * Test class for SessionManager.
 */
@RunWith(RobolectricTestRunner::class)
class UserSessionManagerSpec {
    private val manager = UserSessionManager(
        campaignRepo = mock(CampaignRepository::class.java),
        eventMatchingUtil = mock(EventMatchingUtil::class.java),
        messageReadinessManager = mock(MessageReadinessManager::class.java),
        accountRepo = mock(AccountRepository::class.java),
        pingScheduler = mock(MessageMixerPingScheduler::class.java),
    )

    @Test
    fun `should clear session data if local caching is enabled when calling onSessionUpdate()`() {
        ReflectionHelpers.setStaticField(BuildConfig::class.java, "IS_CACHE_HANDLING", true)

        manager.onSessionUpdate()

        verify(manager.campaignRepo, never()).clearMessages()
        verify(manager.eventMatchingUtil).clearNonPersistentEvents()
        verify(manager.messageReadinessManager).clearMessages()
        verify(manager.accountRepo).clearUserOldCacheStructure()
        verify(manager.pingScheduler).pingMessageMixerService(0)
    }

    @Test
    fun `should clear session data even if local caching is disabled when calling onSessionUpdate()`() {
        ReflectionHelpers.setStaticField(BuildConfig::class.java, "IS_CACHE_HANDLING", false)

        manager.onSessionUpdate()

        verify(manager.campaignRepo).clearMessages()
        verify(manager.eventMatchingUtil).clearNonPersistentEvents()
        verify(manager.messageReadinessManager).clearMessages()
        verify(manager.accountRepo).clearUserOldCacheStructure()
        verify(manager.pingScheduler).pingMessageMixerService(0)
    }
}

