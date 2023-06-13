package com.rakuten.tech.mobile.inappmessaging.runtime.view

import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.test.core.app.ApplicationProvider
import com.nhaarman.mockitokotlin2.verify
import com.rakuten.tech.mobile.inappmessaging.runtime.data.responses.ping.Resource
import com.rakuten.tech.mobile.inappmessaging.runtime.testhelpers.TestDataHelper
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class InAppMessageModalViewSpec {

    @Test
    fun c() {
        val view = spy(InAppMessageModalView(ApplicationProvider.getApplicationContext(), null))

        doReturn(null).`when`(view).findViewById<LinearLayout>(anyInt())

        view.populateViewData(TestDataHelper.createDummyMessage())

        verify(view, never()).setBackgroundColor(anyInt())
    }

    @Test
    fun d() {
        val view = spy(InAppMessageModalView(ApplicationProvider.getApplicationContext(), null))
        val mockModal = mock(LinearLayout::class.java)

//        doReturn(mockModal).`when`(view).modal()
        doReturn(mockModal).`when`(view).findViewById<LinearLayout>(anyInt())
        doReturn(null).`when`(view).findViewById<Button>(anyInt())
        doReturn(null).`when`(view).findViewById<CheckBox>(anyInt())

        view.populateViewData(TestDataHelper.createDummyMessage(
            messagePayload = TestDataHelper.createDummyPayload(
                header = "",
                messageBody = "",
                resource = Resource(imageUrl = "", cropType = 0)
            )
        ))

        verify(mockModal).setBackgroundColor(anyInt())
    }
}