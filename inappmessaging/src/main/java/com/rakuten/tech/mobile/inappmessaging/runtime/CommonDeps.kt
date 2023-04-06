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
internal object CommonDeps {

    fun provideInAppMessaging(): InAppMessaging = InAppMessaging.instance()

    // Repos
    fun provideCampaignRepo(): CampaignRepository = CampaignRepository.instance()

    fun provideConfigRepo(): ConfigResponseRepository = ConfigResponseRepository.instance()

    fun provideHostAppInfoRepo(): HostAppInfoRepository = HostAppInfoRepository.instance()

    fun provideAccountRepo(): AccountRepository = AccountRepository.instance()

    // Managers
    fun provideMessageReadinessManager(): MessageReadinessManager = MessageReadinessManager(
        inAppMessaging = provideInAppMessaging(),
        campaignRepo = provideCampaignRepo(),
        configRepo = provideConfigRepo(),
        hostAppInfoRepo = provideHostAppInfoRepo(),
        accountRepo = provideAccountRepo(),
        pingScheduler = providePingScheduler(),
        viewUtil = ViewUtil,
    )

    // Workers
    fun providePingScheduler(): MessageMixerPingScheduler = MessageMixerPingScheduler.instance()
}
