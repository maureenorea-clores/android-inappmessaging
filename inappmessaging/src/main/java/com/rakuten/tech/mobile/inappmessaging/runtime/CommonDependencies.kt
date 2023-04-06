package com.rakuten.tech.mobile.inappmessaging.runtime

import com.rakuten.tech.mobile.inappmessaging.runtime.data.repositories.AccountRepository
import com.rakuten.tech.mobile.inappmessaging.runtime.data.repositories.CampaignRepository
import com.rakuten.tech.mobile.inappmessaging.runtime.data.repositories.ConfigResponseRepository
import com.rakuten.tech.mobile.inappmessaging.runtime.data.repositories.HostAppInfoRepository
import com.rakuten.tech.mobile.inappmessaging.runtime.manager.MessageReadinessManager
import com.rakuten.tech.mobile.inappmessaging.runtime.utils.ViewUtil
import com.rakuten.tech.mobile.inappmessaging.runtime.workmanager.schedulers.MessageMixerPingScheduler

/**
 * Object containing common dependencies instances, inject these to dependent classes.
 */
internal object CommonDependencies {

    val inAppMessaging: InAppMessaging by lazy {
        InAppMessaging.instance()
    }

    // Repos
    val campaignRepo: CampaignRepository by lazy {
        CampaignRepository.instance()
    }

    val configRepo: ConfigResponseRepository by lazy {
        ConfigResponseRepository.instance()
    }

    val hostAppInfoRepo: HostAppInfoRepository by lazy {
        HostAppInfoRepository.instance()
    }

    val accountRepo: AccountRepository by lazy {
        AccountRepository.instance()
    }

    // Managers
    val messageReadinessManager: MessageReadinessManager by lazy {
        MessageReadinessManager(
            inAppMessaging = inAppMessaging,
            campaignRepo = campaignRepo,
            configRepo = configRepo,
            hostAppInfoRepo = hostAppInfoRepo,
            accountRepo = accountRepo,
            pingScheduler = pingScheduler,
            viewUtil = ViewUtil,
        )
    }

    // Workers
    val pingScheduler: MessageMixerPingScheduler by lazy {
        MessageMixerPingScheduler.instance()
    }
}
