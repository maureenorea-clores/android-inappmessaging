package com.rakuten.tech.mobile.inappmessaging.runtime

import android.content.Context
import android.content.res.Resources
import androidx.test.core.app.ApplicationProvider
import com.nhaarman.mockitokotlin2.any
import com.rakuten.tech.mobile.inappmessaging.runtime.utils.CommonUtil
import org.amshove.kluent.shouldBeEqualTo
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RmcHelperSpec {

    private val mockCommonUtil = mockStatic(CommonUtil::class.java)

    @After
    fun tearDown() {
        mockCommonUtil.close()
    }

    @Test
    fun `isRmcIntegrated should return true`() {
        mockCommonUtil.`when`<Any> { CommonUtil.hasClass(any()) }.thenReturn(true)

        RmcHelper.isRmcIntegrated() shouldBeEqualTo true
    }

    @Test
    fun `isRmcIntegrated should return false`() {
        mockCommonUtil.`when`<Any> { CommonUtil.hasClass(any()) }.thenReturn(false)

        RmcHelper.isRmcIntegrated() shouldBeEqualTo false
    }

    @Test
    fun `getRmcVersion should return version from resource appended with suffix`() {
        val mockContext = mock(Context::class.java)
        `when`(mockContext.resources).thenReturn(mock(Resources::class.java))
        `when`(mockContext.getString(anyInt())).thenReturn("1.0.0")

        RmcHelper.getRmcVersion(mockContext) shouldBeEqualTo "1.0.0${RmcHelper.RMC_SUFFIX}"
    }

    @Test
    fun `getRmcVersion should return null if resource does not exist`() {
        RmcHelper.getRmcVersion(ApplicationProvider.getApplicationContext()) shouldBeEqualTo null
    }
}
