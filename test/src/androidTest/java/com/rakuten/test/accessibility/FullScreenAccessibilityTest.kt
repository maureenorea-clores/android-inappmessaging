package com.rakuten.test.accessibility

import androidx.test.espresso.accessibility.AccessibilityChecks
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResult
import com.rakuten.test.helpers.Constants
import com.rakuten.test.helpers.Utils
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FullScreenAccessibilityTest {
    private var mockServer = MockWebServer()

    @Before
    fun beforeTest() {
        mockServer.start(Constants.PORT)
        mockServer.url(Constants.PATH)
    }

    @After
    fun afterTest() {
        mockServer.shutdown()
    }

    @Test
    fun fullScreenTextOnly() {
        Utils.performNormalFlow(mockServer,"full-text-only.json")
    }

    @Test
    fun fullScreenImageOnly() {
        Utils.performNormalFlow(mockServer,"full-image-only.json", 3000)
    }

    @Test
    fun fullScreenTextImage() {
        Utils.performNormalFlow(mockServer,"full-text-image.json")
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            AccessibilityChecks.enable()
                .setRunChecksFromRootView(true)
                .setThrowExceptionFor(AccessibilityCheckResult.AccessibilityCheckResultType.WARNING)
        }
    }
}
