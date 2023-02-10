package com.rakuten.tech.mobile.inappmessaging.runtime

import org.junit.After
import org.junit.Before
import org.mockito.Mockito

/**
 * Base test class of all test classes.
 */
open class BaseTest {
    @Before
    open fun setup() {
        InAppMessaging.errorCallback = null
        InAppMessaging.setNotConfiguredInstance()
    }

    /**
     * See [Memory leak in mockito-inline...](https://github.com/mockito/mockito/issues/1614)
     */
    @After
    open fun tearDown() {
        Mockito.framework().clearInlineMocks()
        InAppMessaging.errorCallback = null
        InAppMessaging.setNotConfiguredInstance()
    }
}
