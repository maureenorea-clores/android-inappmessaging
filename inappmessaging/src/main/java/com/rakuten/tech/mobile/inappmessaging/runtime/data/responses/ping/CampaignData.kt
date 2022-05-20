package com.rakuten.tech.mobile.inappmessaging.runtime.data.responses.ping

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.rakuten.tech.mobile.inappmessaging.runtime.data.models.messages.Message

/**
 * Class for parsing CampaignData, which is a response from MessageMixer.
 */
@SuppressWarnings("TooManyFunctions")
@JsonClass(generateAdapter = true)
internal data class CampaignData(
    @Json(name = "messagePayload")
    private val messagePayload: MessagePayload,
    @Json(name = "type")
    private val type: Int,
    @Json(name = "triggers")
    private val triggers: List<Trigger>?,
    @Json(name = "campaignId")
    private val campaignId: String,
    @Json(name = "isTest")
    private val isTest: Boolean,
    @Json(name = "maxImpressions")
    private var maxImpressions: Int = 0,
    @Json(name = "hasNoEndDate")
    private val hasNoEndDate: Boolean = false,
    @Json(name = "isCampaignDismissable")
    private val isCampaignDismissable: Boolean = true,
    @Json(name = "infiniteImpressions")
    private val infiniteImpressions: Boolean = false
) : Message {

    @Json(name = "timesClosed")
    internal var timesClosed = 0

    override fun getType(): Int = type

    override fun getCampaignId(): String = campaignId

    override fun getTriggers(): List<Trigger>? = triggers

    override fun getMessagePayload(): MessagePayload = messagePayload

    override fun isTest(): Boolean = isTest

    override fun getMaxImpressions(): Int = maxImpressions

    override fun setMaxImpression(maxImpression: Int) {
        this.maxImpressions = maxImpression
    }

    override fun getContexts(): List<String> {
        val regex = Regex("\\[(.*?)\\]")
        val matches = regex.findAll(messagePayload.title)
        return matches.map { it.groupValues[1] }.toList()
    }

    override fun getNumberOfTimesClosed() = synchronized(timesClosed) {
        timesClosed
    }
    override fun incrementTimesClosed() {
        synchronized(timesClosed) {
            timesClosed++
        }
    }

    override fun infiniteImpressions() = infiniteImpressions

    override fun hasNoEndDate() = hasNoEndDate

    override fun isCampaignDismissable() = isCampaignDismissable
}
