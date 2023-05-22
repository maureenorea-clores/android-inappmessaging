package com.example.rmc_iam

import android.content.Context
import com.rakuten.tech.mobile.inappmessaging.runtime.InAppMessaging

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

    fun instance() = InAppMessaging.instance()

    var errorCallback = InAppMessaging.errorCallback
}