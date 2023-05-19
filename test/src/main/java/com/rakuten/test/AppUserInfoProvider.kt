package com.rakuten.test

import com.example.rmc_iam.UserInfoProvider

class AppUserInfoProvider : UserInfoProvider {

    var userId = ""
    var accessToken = ""
    var idTracking = ""
    
    override fun provideAccessToken() = accessToken

    override fun provideUserId() = userId

    override fun provideIdTrackingIdentifier() = idTracking
}