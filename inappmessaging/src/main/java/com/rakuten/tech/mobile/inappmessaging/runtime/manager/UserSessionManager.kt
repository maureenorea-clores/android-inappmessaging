package com.rakuten.tech.mobile.inappmessaging.runtime.manager

import com.rakuten.tech.mobile.inappmessaging.runtime.BuildConfig
import com.rakuten.tech.mobile.inappmessaging.runtime.data.repositories.AccountRepository
import com.rakuten.tech.mobile.inappmessaging.runtime.data.repositories.CampaignRepository
import com.rakuten.tech.mobile.inappmessaging.runtime.utils.EventMatchingUtil
import com.rakuten.tech.mobile.inappmessaging.runtime.utils.RetryDelayUtil
import com.rakuten.tech.mobile.inappmessaging.runtime.workmanager.schedulers.MessageMixerPingScheduler

/**
 * SessionManager, it is the manager of session tracking. It will discard old message data, and
 * prepare new message data for the new user or session.
 */
internal interface SessionManager {
    /**
     * Upon login successful or logout, old messages will be discarded, then prepare new messages for the new
     * user.
     */
    fun onSessionUpdate()
}

internal class UserSessionManager(
    val campaignRepo: CampaignRepository,
    val eventMatchingUtil: EventMatchingUtil,
    val messageReadinessManager: MessageReadinessManager,
    val accountRepo: AccountRepository,
    val pingScheduler: MessageMixerPingScheduler,
) : SessionManager {
    override fun onSessionUpdate() {
        if (!BuildConfig.IS_CACHE_HANDLING) {
            // Clear locally stored campaigns from ping response
            campaignRepo.clearMessages()
        }

        // Clear matched events
        eventMatchingUtil.clearNonPersistentEvents()

        // Clear campaigns which are ready for display
        messageReadinessManager.clearMessages()

        // Clear any stale user cache structure if applicable
        accountRepo.clearUserOldCacheStructure()

        // reset current delay to initial
        // future update: possibly add checking if last ping is within a certain threshold before executing the request
        MessageMixerPingScheduler.currDelay = RetryDelayUtil.INITIAL_BACKOFF_DELAY
        pingScheduler.pingMessageMixerService(0)
    }

    companion object {
        private val instance: UserSessionManager = UserSessionManager(
            CampaignRepository.instance(),
            EventMatchingUtil.instance(),
            MessageReadinessManager.instance(),
            AccountRepository.instance(),
            MessageMixerPingScheduler.instance(),
        )
        fun instance() = instance
    }
}