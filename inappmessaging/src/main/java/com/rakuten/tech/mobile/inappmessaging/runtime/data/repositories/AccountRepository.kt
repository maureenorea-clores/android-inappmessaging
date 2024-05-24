package com.rakuten.tech.mobile.inappmessaging.runtime.data.repositories

import android.annotation.SuppressLint
import com.rakuten.tech.mobile.inappmessaging.runtime.BuildConfig
import com.rakuten.tech.mobile.inappmessaging.runtime.UserInfoProvider
import com.rakuten.tech.mobile.inappmessaging.runtime.data.enums.UserIdentifierType
import com.rakuten.tech.mobile.inappmessaging.runtime.data.models.UserIdentifier
import com.rakuten.tech.mobile.inappmessaging.runtime.utils.InAppLogger
import java.math.BigInteger
import java.security.MessageDigest

/**
 * This object contains userInfoProvider ID used when logging in, and Access token.
 * Note: Never persist account information without encrypting first.
 */
internal abstract class AccountRepository {
    var userInfoProvider: UserInfoProvider? = null

    /**
     * This method returns access token, or empty String.
     */
    abstract fun getAccessToken(): String

    /**
     * This method returns User ID, or empty String.
     */
    abstract fun getUserId(): String

    /**
     * This method returns ID tracking identifier, or empty String.
     */
    abstract fun getIdTrackingIdentifier(): String

    abstract fun logWarningForUserInfo(tag: String, logger: InAppLogger = InAppLogger(tag))

    /**
     * This method retrieves the encrypted version of the [userInfoProvider].
     */
    abstract fun getEncryptedUserFromProvider(): String

    /**
     * This is a helper method to retrieve encrypted version of [userIds] that is used during ping request.
     */
    abstract fun getEncryptedUserFromUserIds(userIds: List<UserIdentifier>): String

    @SuppressWarnings("kotlin:S6515")
    companion object {
        private const val TOKEN_PREFIX = "OAuth2 "
        internal const val ID_TRACKING_ERR_MSG = "Both an access token and a user tracking id have been set. " +
            "Only one of these id types is expected to be set at the same time"
        internal const val TOKEN_USER_ERR_MSG = "User Id must be present and not empty when access token is specified"

        private var instance: AccountRepository = AccountRepositoryImpl()

        fun instance() = instance
    }

    private class AccountRepositoryImpl : AccountRepository() {
        override fun getAccessToken() = if (this.userInfoProvider == null ||
            this.userInfoProvider?.provideAccessToken().isNullOrEmpty()
        ) {
            ""
        } else {
            TOKEN_PREFIX + this.userInfoProvider?.provideAccessToken()
        }
        // According to backend specs, token has to start with "OAuth2{space}", followed by real token.

        override fun getUserId() = this.userInfoProvider?.provideUserId().orEmpty()

        override fun getIdTrackingIdentifier() = this.userInfoProvider?.provideIdTrackingIdentifier().orEmpty()

        @SuppressLint("BinaryOperationInTimber")
        override fun logWarningForUserInfo(tag: String, logger: InAppLogger) {
            if (getAccessToken().isNotEmpty()) {
                if (getIdTrackingIdentifier().isNotEmpty()) {
                    logger.warn(ID_TRACKING_ERR_MSG)
                    if (BuildConfig.DEBUG) {
                        error(ID_TRACKING_ERR_MSG)
                    }
                }
                if (getUserId().isEmpty()) {
                    logger.warn(TOKEN_USER_ERR_MSG)
                    if (BuildConfig.DEBUG) {
                        error(TOKEN_USER_ERR_MSG)
                    }
                }
            }
        }

        override fun getEncryptedUserFromProvider(): String {
            val user = hash(getUserId() + getIdTrackingIdentifier())
            InAppLogger(TAG).debug("User from provider: $user")
            return user
        }

        override fun getEncryptedUserFromUserIds(userIds: List<UserIdentifier>): String {
            var userId = ""
            var idTracking = ""

            for (identifier in userIds) {
                if (identifier.type == UserIdentifierType.USER_ID.typeId) {
                    userId = identifier.id
                } else if (identifier.type == UserIdentifierType.ID_TRACKING.typeId) {
                    idTracking = identifier.id
                }
            }

            val user = hash(userId + idTracking)
            InAppLogger(TAG).debug("User from userIdentifiers: $user")
            return user
        }

        @SuppressWarnings("TooGenericExceptionCaught")
        private fun hash(input: String, algo: String? = null): String {
            return try {
                // MD5 hashing
                val bytes = MessageDigest
                    .getInstance(algo ?: "MD5")
                    .digest(input.toByteArray())

                BigInteger(1, bytes).toString(RADIX).padStart(PAD_LENGTH, '0')
            } catch (ex: Exception) {
                // should never happen since "MD5" is a supported algorithm
                InAppLogger(TAG).debug(ex.message)
                input
            }
        }

        companion object {
            private const val TAG = "IAM_AccountRepository"
            private const val RADIX = 16
            private const val PAD_LENGTH = 32
        }
    }
}
