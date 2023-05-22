package com.example.rmc_iam

interface RmcUserInfoProvider {
    fun provideAccessToken(): String? = ""
    fun provideUserId(): String? = ""
    fun provideIdTrackingIdentifier(): String? = ""
}