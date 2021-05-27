package com.rakuten.tech.mobile.inappmessaging.runtime.data.repositories

import com.google.gson.Gson
import com.rakuten.tech.mobile.inappmessaging.runtime.BaseTest
import com.rakuten.tech.mobile.inappmessaging.runtime.data.responses.config.ConfigResponse
import com.rakuten.tech.mobile.inappmessaging.runtime.data.responses.config.ConfigResponseData
import org.amshove.kluent.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import kotlin.random.Random

/**
 * Test class for ConfigResponseRepository class.
 */
class ConfigResponseRepositorySpec : BaseTest() {

    @Before
    fun setup() {
        ConfigResponseRepository.resetInstance()
    }

    @Test(expected = IllegalArgumentException::class)
    fun `should throw exception for null data`() {
        ConfigResponseRepository.instance().addConfigResponse(null)
    }

    @Test
    fun `should be empty string for impression endpoints with initial values`() {
        ConfigResponseRepository.instance().getImpressionEndpoint() shouldBeEqualTo ""
    }

    @Test
    fun `should be empty string for display endpoints with initial values`() {
        ConfigResponseRepository.instance().getDisplayPermissionEndpoint() shouldBeEqualTo ""
    }

    @Test
    fun `should be empty string for ping endpoints with initial values`() {
        ConfigResponseRepository.instance().getPingEndpoint() shouldBeEqualTo ""
    }

    @Test
    fun `should be valid value for impression endpoints with initial values`() {
        val response = Gson().fromJson(CONFIG_RESPONSE.trimIndent(), ConfigResponse::class.java)
        ConfigResponseRepository.instance().addConfigResponse(response.data)
        ConfigResponseRepository.instance().getImpressionEndpoint() shouldBeEqualTo response.data?.endpoints?.impression
    }

    @Test
    fun `should be valid value for display endpoints with initial values`() {
        val response = Gson().fromJson(CONFIG_RESPONSE.trimIndent(), ConfigResponse::class.java)
        ConfigResponseRepository.instance().addConfigResponse(response.data)
        ConfigResponseRepository.instance()
                .getDisplayPermissionEndpoint() shouldBeEqualTo response.data?.endpoints?.displayPermission
    }

    @Test
    fun `should be valid value for ping endpoints with initial values`() {
        val response = Gson().fromJson(CONFIG_RESPONSE.trimIndent(), ConfigResponse::class.java)
        ConfigResponseRepository.instance().addConfigResponse(response.data)
        ConfigResponseRepository.instance().getPingEndpoint() shouldBeEqualTo response.data?.endpoints?.ping
    }

    @Test
    fun `should return correct enable settings due to roll out percentage`() {
        var response = Gson().fromJson(CONFIG_RESPONSE.trimIndent(), ConfigResponse::class.java)
        ConfigResponseRepository.instance().addConfigResponse(response.data)
        ConfigResponseRepository.instance().isConfigEnabled().shouldBeTrue()

        response = Gson().fromJson(CONFIG_DISABLED_RESPONSE.trimIndent(), ConfigResponse::class.java)
        ConfigResponseRepository.instance().addConfigResponse(response.data)
        ConfigResponseRepository.instance().isConfigEnabled().shouldBeFalse()
    }

    @Test
    fun `should return correct enable settings due to roll out between 0 and 100`() {
        val mockConfig = Mockito.mock(ConfigResponseData::class.java)
        val mockRandomizer = Mockito.mock(Random::class.java)

        ConfigResponseRepository.randomizer = mockRandomizer

        When calling mockConfig.rollOutPercentage itReturns 50
        When calling mockRandomizer.nextInt(1, 101) itReturns 50

        ConfigResponseRepository.instance().addConfigResponse(mockConfig)
        ConfigResponseRepository.instance().isConfigEnabled().shouldBeTrue()

        When calling mockRandomizer.nextInt(1, 101) itReturns 49

        ConfigResponseRepository.instance().addConfigResponse(mockConfig)
        ConfigResponseRepository.instance().isConfigEnabled().shouldBeTrue()

        When calling mockRandomizer.nextInt(1, 101) itReturns 51

        ConfigResponseRepository.instance().addConfigResponse(mockConfig)
        ConfigResponseRepository.instance().isConfigEnabled().shouldBeFalse()
    }

    companion object {
        private const val CONFIG_RESPONSE = """{
            "data":{
                "rolloutPercentage":100,
                "endpoints":{
                    "displayPermission":"https://sample.display.permission",
                    "impression":"https://sample.impression",
                    "ping":"https://sample.ping"
                }
            }
        }"""

        private const val CONFIG_DISABLED_RESPONSE = """{
            "data":{
                "rolloutPercentage":0,
                "endpoints":{
                    "displayPermission":"https://sample.display.permission",
                    "impression":"https://sample.impression",
                    "ping":"https://sample.ping"
                }
            }
        }"""
    }
}
