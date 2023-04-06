package com.rakuten.tech.mobile.inappmessaging.runtime.testhelpers

import com.rakuten.tech.mobile.inappmessaging.runtime.InAppMessaging
import com.rakuten.tech.mobile.inappmessaging.runtime.data.repositories.AccountRepository
import com.rakuten.tech.mobile.inappmessaging.runtime.data.repositories.CampaignRepository
import com.rakuten.tech.mobile.inappmessaging.runtime.data.repositories.ConfigResponseRepository
import com.rakuten.tech.mobile.inappmessaging.runtime.data.repositories.HostAppInfoRepository
import com.rakuten.tech.mobile.inappmessaging.runtime.manager.MessageReadinessManager
import com.rakuten.tech.mobile.inappmessaging.runtime.utils.ResourceUtils
import com.rakuten.tech.mobile.inappmessaging.runtime.workmanager.schedulers.MessageMixerPingScheduler
import org.mockito.Mockito.mock

internal object MockHelper {

    val inAppMessaging = mock(InAppMessaging::class.java)

    // Repos
    val campaignRepo = mock(CampaignRepository::class.java)

    val configRepo = mock(ConfigResponseRepository::class.java)

    val hostAppInfoRepo = mock(HostAppInfoRepository::class.java)

    val accountRepo = mock(AccountRepository::class.java)

    // Managers
    val messageReadinessManager = mock(MessageReadinessManager::class.java)

    // Workers
    val pingScheduler = mock(MessageMixerPingScheduler::class.java)

    // Utils
    val resourceUtils = mock(ResourceUtils::class.java)
}