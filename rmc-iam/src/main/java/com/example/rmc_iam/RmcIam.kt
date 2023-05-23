package com.example.rmc_iam

import android.app.Activity
import android.content.Context
import com.rakuten.tech.mobile.inappmessaging.runtime.InAppMessaging
import java.util.Date

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

    fun logEvent(event: AppStartEvent) {
        InAppMessaging.instance().logEvent(event)
    }

    fun logEvent(event: LoginSuccessfulEvent) {
        InAppMessaging.instance().logEvent(event)
    }

    fun logEvent(event: PurchaseSuccessfulEvent) {
        val iamEvent = com.rakuten.tech.mobile.inappmessaging.runtime.data.models.appevents.PurchaseSuccessfulEvent()
        event.purchaseAmountMicros?.let { iamEvent.purchaseAmountMicros(it) }
        event.numberOfItems?.let { iamEvent.numberOfItems(it) }
        event.currencyCode?.let { iamEvent.currencyCode(it) }
        event.itemIdList?.let { iamEvent.itemIdList(it) }
        InAppMessaging.instance().logEvent(iamEvent)
    }

    fun logEvent(event: CustomEvent) {
        val iamEvent = com.rakuten.tech.mobile.inappmessaging.runtime.data.models.appevents.CustomEvent(event.getEventName())

        event.attributes?.let {
            it.forEach { entry ->
                when(entry.value) {
                    is Date -> iamEvent.addAttribute(entry.key, entry.value as Date)
                    is Int -> iamEvent.addAttribute(entry.key, entry.value as Int)
                    is Double -> iamEvent.addAttribute(entry.key, entry.value as Double)
                    is String -> iamEvent.addAttribute(entry.key, entry.value as String)
                    is Boolean -> iamEvent.addAttribute(entry.key, entry.value as Boolean)
                }
            }
        }

        InAppMessaging.instance().logEvent(iamEvent)
    }

    var errorCallback = InAppMessaging.errorCallback

    var onVerifyContext: (contexts: List<String>, campaignTitle: String) -> Boolean = InAppMessaging.instance().onVerifyContext

    var onPushPrimer: (() -> Unit)? = InAppMessaging.instance().onPushPrimer
}

class AppStartEvent: com.rakuten.tech.mobile.inappmessaging.runtime.data.models.appevents.AppStartEvent()

class LoginSuccessfulEvent: com.rakuten.tech.mobile.inappmessaging.runtime.data.models.appevents.LoginSuccessfulEvent()

class CustomEvent(
    eventName: String,
    val attributes: Map<String, Any>? = null
) : com.rakuten.tech.mobile.inappmessaging.runtime.data.models.appevents.CustomEvent(
    eventName
) {

}

open class CustomOnTouchListener: com.rakuten.tech.mobile.inappmessaging.runtime.view.CustomOnTouchListener()

class PurchaseSuccessfulEvent(
    val purchaseAmountMicros: Int? = null,
    val numberOfItems: Int? = null,
    val currencyCode: String? = null,
    val itemIdList: List<String>? = null
): com.rakuten.tech.mobile.inappmessaging.runtime.data.models.appevents.PurchaseSuccessfulEvent() {
}