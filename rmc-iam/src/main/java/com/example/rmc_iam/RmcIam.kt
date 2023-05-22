package com.example.rmc_iam

import android.content.Context
import com.rakuten.tech.mobile.inappmessaging.runtime.InAppMessaging

object RmcIam {

    fun instance() = InAppMessaging.instance()

    var errorCallback = InAppMessaging.errorCallback

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
}

class AppStartEvent: com.rakuten.tech.mobile.inappmessaging.runtime.data.models.appevents.AppStartEvent()

class LoginSuccessfulEvent: com.rakuten.tech.mobile.inappmessaging.runtime.data.models.appevents.LoginSuccessfulEvent()

class PurchaseSuccessfulEvent: com.rakuten.tech.mobile.inappmessaging.runtime.data.models.appevents.PurchaseSuccessfulEvent()

class CustomEvent(eventName: String) : com.rakuten.tech.mobile.inappmessaging.runtime.data.models.appevents.CustomEvent(
    eventName
)