package com.example.rmc_iam

import android.app.Activity
import android.content.Context
import com.rakuten.tech.mobile.inappmessaging.runtime.InAppMessaging
import com.rakuten.tech.mobile.inappmessaging.runtime.data.models.appevents.Event

object RmcIam {
    fun configure(
        context: Context,
        subscriptionKey: String? = null,
        configUrl: String? = null,
        enableTooltipFeature: Boolean? = false,
    ): Boolean {
        return InAppMessaging.configure(
            context,
            subscriptionKey,
            configUrl,
            enableTooltipFeature,
        )
    }

    fun registerPreference(userInfoProvider: RmcUserInfoProvider) {

        class IamUserInfoProvider: com.rakuten.tech.mobile.inappmessaging.runtime.UserInfoProvider {
            override fun provideAccessToken() = userInfoProvider.provideAccessToken()
            override fun provideUserId() = userInfoProvider.provideUserId()
            override fun provideIdTrackingIdentifier() = userInfoProvider.provideIdTrackingIdentifier()
        }


        InAppMessaging.instance().registerPreference(IamUserInfoProvider())
    }

    // Won't work since [InAppMessaging] is only accessible in this module and not on app module.
    // Will result to:  Cannot access class 'com.rakuten.tech.mobile.inappmessaging.runtime.InAppMessaging'. Check your module classpath for missing or conflicting dependencies.
    //fun instance() = InAppMessaging.instance()

    fun registerMessageDisplayActivity(activity: Activity) {
        InAppMessaging.instance().registerMessageDisplayActivity(activity)
    }

    fun unregisterMessageDisplayActivity() {
        InAppMessaging.instance().unregisterMessageDisplayActivity()
    }

    fun closeMessage(clearQueuedCampaigns: Boolean = false) {
        InAppMessaging.instance().closeMessage(clearQueuedCampaigns)
    }

    fun closeTooltip(viewId: String) {
        InAppMessaging.instance().closeTooltip(viewId)
    }

    fun logEvent(event: Event) {
        InAppMessaging.instance().logEvent(event)
    }

    var errorCallback = InAppMessaging.errorCallback

    var onVerifyContext: (contexts: List<String>, campaignTitle: String) -> Boolean = InAppMessaging.instance().onVerifyContext

    var onPushPrimer: (() -> Unit)? = InAppMessaging.instance().onPushPrimer
}

class AppStartEvent: com.rakuten.tech.mobile.inappmessaging.runtime.data.models.appevents.AppStartEvent()

class LoginSuccessfulEvent: com.rakuten.tech.mobile.inappmessaging.runtime.data.models.appevents.LoginSuccessfulEvent()

class PurchaseSuccessfulEvent: com.rakuten.tech.mobile.inappmessaging.runtime.data.models.appevents.PurchaseSuccessfulEvent()

class CustomEvent(eventName: String) : com.rakuten.tech.mobile.inappmessaging.runtime.data.models.appevents.CustomEvent(
    eventName
)

interface UserInfoProvider: com.rakuten.tech.mobile.inappmessaging.runtime.UserInfoProvider

open class CustomOnTouchListener: com.rakuten.tech.mobile.inappmessaging.runtime.view.CustomOnTouchListener()