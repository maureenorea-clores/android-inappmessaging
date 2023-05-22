package com.rakuten.test

import android.app.Application
import com.example.rmc_iam.RmcIam
//import com.rakuten.tech.mobile.inappmessaging.runtime.InAppMessaging

class MainApplication : Application() {

    val provider = AppUserInfoProvider()
    lateinit var settings: IAMSettings

    override fun onCreate() {
        super.onCreate()
        settings = IAMSettings(this)
        RmcIam.configure(this, enableTooltipFeature = settings.isTooltipFeatureEnabled)
        RmcIam.instance().registerPreference(provider)
    }
}