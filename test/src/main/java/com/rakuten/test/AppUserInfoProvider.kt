package com.rakuten.test

import com.example.rmc_iam.RmcUserInfoProvider

class AppUserInfoProvider : RmcUserInfoProvider {

    var userId = ""
    var accessToken = ""
    var idTracking = ""
    
    override fun provideAccessToken() = accessToken

    override fun provideUserId() = userId

    override fun provideIdTrackingIdentifier() = idTracking
}