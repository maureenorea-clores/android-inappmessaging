package com.rakuten.tech.mobile.inappmessaging.runtime.data.repositories

import android.content.Context
import com.google.gson.Gson
import com.rakuten.tech.mobile.inappmessaging.runtime.InAppMessaging
import com.rakuten.tech.mobile.inappmessaging.runtime.data.enums.InAppMessageType
import com.rakuten.tech.mobile.inappmessaging.runtime.data.models.UserIdentifier
import com.rakuten.tech.mobile.inappmessaging.runtime.data.responses.ping.Message
import com.rakuten.tech.mobile.inappmessaging.runtime.utils.InAppLogger
import com.rakuten.tech.mobile.sdkutils.PreferencesUtil
import org.json.JSONObject
import java.lang.Integer.max

internal abstract class CampaignRepository {
    val messages: LinkedHashMap<String, Message> = linkedMapOf()
    var lastSyncMillis: Long? = null

    /**
     * Syncs [messageList] with server.
     */
    abstract fun syncWith(identifiers: List<UserIdentifier>, messageList: List<Message>, timestampMillis: Long,
                          ignoreTooltips: Boolean = false)

    /**
     * Updates the [Message.isOptedOut] as true for the provided campaign.
     */
    abstract fun optOutCampaign(campaign: Message): Message?

    /**
     * Decrements the number of [Message.impressionsLeft] for provided campaign Id in the repository.
     */
    abstract fun decrementImpressions(id: String): Message?

    /**
     * Increments the number of [Message.impressionsLeft] for provided campaign Id in the repository.
     */
    abstract fun incrementImpressions(id: String): Message?

    /**
     * Clears messages for last user.
     */
    abstract fun clear()

    abstract fun isSyncedWithCurrentUserInfo(): Boolean

    @SuppressWarnings("kotlin:S6515")
    companion object {
        private var instance: CampaignRepository = CampaignRepositoryImpl(AccountRepository.instance())

        internal const val IAM_USER_CACHE = "IAM_user_cache"
        private const val TAG = "IAM_CampaignRepo"
        private const val IAM_USER_CACHE_PREFIX = "internal_shared_prefs_"

        fun instance(): CampaignRepository = instance
    }

    @SuppressWarnings(
        "TooManyFunctions",
    )
    private class CampaignRepositoryImpl(val accountRepo: AccountRepository): CampaignRepository() {

        private var lastSyncCache: String? = null

        override fun isSyncedWithCurrentUserInfo(): Boolean {
            val synced = lastSyncCache != null && lastSyncCache == accountRepo.getEncryptedUserFromProvider()
            InAppLogger(TAG).debug("Repo synced: $synced")
            return synced
        }

        @Synchronized
        override fun syncWith(identifiers: List<UserIdentifier>, messageList: List<Message>, timestampMillis: Long,
            ignoreTooltips: Boolean) {

            lastSyncMillis = timestampMillis
            lastSyncCache = accountRepo.getEncryptedUserFromUserIds(identifiers)

            InAppLogger(TAG).debug("START - user: $lastSyncCache")

            loadCachedData() // ensure we're using latest cache data for syncing below
            val oldList = LinkedHashMap(messages) // copy

            messages.clear()
            for (newCampaign in messageList.filterMessages(ignoreTooltips)) {
                val updatedCampaign = updateCampaign(newCampaign, oldList)
                messages[updatedCampaign.campaignId] = updatedCampaign
            }
            saveDataToCache()
            InAppLogger(TAG).debug("END")
        }

        private fun List<Message>.filterMessages(ignoreTooltips: Boolean): List<Message> {
            return this.filterNot {
                it.campaignId.isEmpty() || (it.type == InAppMessageType.TOOLTIP.typeId && ignoreTooltips)
            }
        }

        private fun updateCampaign(newCampaign: Message, oldList: LinkedHashMap<String, Message>): Message {
            val oldCampaign = oldList[newCampaign.campaignId]
            if (oldCampaign != null) {
                newCampaign.isOptedOut = (oldCampaign.isOptedOut == true)

                var newImpressionsLeft = oldCampaign.impressionsLeft ?: oldCampaign.maxImpressions
                val isMaxImpressionsEdited = oldCampaign.maxImpressions != newCampaign.maxImpressions
                if (isMaxImpressionsEdited) {
                    newImpressionsLeft += newCampaign.maxImpressions - oldCampaign.maxImpressions
                }
                newImpressionsLeft = max(0, newImpressionsLeft)
                newCampaign.impressionsLeft = newImpressionsLeft
            }
            return newCampaign
        }

        override fun clear() {
            messages.clear()
            lastSyncCache = null
        }

        override fun optOutCampaign(campaign: Message): Message? {
            InAppLogger(TAG).debug("Campaign: ${campaign.campaignId}, user: $lastSyncCache")
            val localCampaign = messages[campaign.campaignId]
            if (localCampaign == null) {
                InAppLogger(TAG).debug("Campaign (${campaign.campaignId}) could not be updated - not found " +
                        "in the repository")
                return null
            }
            val updatedCampaign = localCampaign.apply { isOptedOut = true }
            if (!campaign.isTest) {
                saveDataToCache()
            }
            return updatedCampaign
        }

        override fun decrementImpressions(id: String): Message? {
            InAppLogger(TAG).debug("Campaign: $id, user: $lastSyncCache")
            val campaign = messages[id] ?: return null
            return updateImpressions(
                campaign,
                max(0, (campaign.impressionsLeft ?: campaign.maxImpressions) - 1),
            )
        }

        // For testing purposes
        override fun incrementImpressions(id: String): Message? {
            val campaign = messages[id] ?: return null
            return updateImpressions(
                campaign,
                (campaign.impressionsLeft ?: campaign.maxImpressions) + 1,
            )
        }

        @SuppressWarnings("TooGenericExceptionCaught")
        private fun loadCachedData() {
            if (InAppMessaging.instance().isLocalCachingEnabled()) {
                InAppLogger(TAG).debug("START - user: $lastSyncCache")
                messages.clear()
                try {
                    val preferenceData = retrieveData()
                    if (preferenceData.isEmpty()) {
                        InAppLogger(TAG).debug("Cache data is empty")
                        return
                    }

                    val jsonObject = JSONObject(preferenceData)
                    for (key in jsonObject.keys()) {
                        messages[key] = Gson().fromJson(jsonObject.getJSONObject(key).toString(), Message::class.java)
                    }
                } catch (ex: Exception) {
                    InAppLogger(TAG).debug(ex.cause, "Invalid JSON format for $IAM_USER_CACHE data")
                }
                InAppLogger(TAG).debug("END")
            }
        }

        private fun retrieveData(): String {
            return HostAppInfoRepository.instance().getContext()?.let { ctx ->
                val preferenceData = PreferencesUtil.getString(
                    context = ctx,
                    name = "${IAM_USER_CACHE_PREFIX}${lastSyncCache}",
                    key = IAM_USER_CACHE,
                    defValue = "",
                )
                InAppLogger(TAG).debug("Cache Read - user: $lastSyncCache, data: $preferenceData")
                preferenceData
            }.orEmpty()
        }

        private fun saveDataToCache() {
            if (!InAppMessaging.instance().isLocalCachingEnabled())
                return

            val context = HostAppInfoRepository.instance().getContext() ?: return

            InAppLogger(TAG).debug("START - user: $lastSyncCache")

            val sharedPrefs = context.getSharedPreferences("${IAM_USER_CACHE_PREFIX}${lastSyncCache}",
                Context.MODE_PRIVATE)
            val preferenceData = Gson().toJson(messages)

            InAppLogger(TAG).debug("Cache write: $preferenceData")
            sharedPrefs.edit().apply {
                clear() // will also clear stale structure which existed prior to v7.2.0
                putString(IAM_USER_CACHE, preferenceData)
                apply()
            }

            InAppLogger(TAG).debug("END - user: $lastSyncCache")
        }

        private fun updateImpressions(campaign: Message, newValue: Int): Message {
            val updatedCampaign = campaign.apply { impressionsLeft = newValue }
            messages[campaign.campaignId] = updatedCampaign

            saveDataToCache()
            return updatedCampaign
        }
    }
}
