package com.rakuten.tech.mobile.inappmessaging.runtime

import android.content.Context
import com.rakuten.tech.mobile.inappmessaging.runtime.utils.CommonUtil
import com.rakuten.tech.mobile.inappmessaging.runtime.utils.InAppLogger

/**
 * Contains methods related to the RMC In-App Messaging SDK.
 */
internal object RmcHelper {

    /**
     * Suffix to isolate whether IAM API call came from RMC SDK.
     */
    const val RMC_SUFFIX = "-rmc"

    private const val TAG = "RmcHelper"

    /**
     * Checks if app is using RMC SDK by checking the existence of its main entry point public class.
     */
    @JvmStatic
    fun isRmcIntegrated() = CommonUtil.hasClass("com.rakuten.tech.mobile.rmc.Rmc")

    /**
     * Returns the RMC SDK version through the resource identifier, appended with [RMC_SUFFIX]. e.g. 1.0.0-rmc
     *
     * @return the RMC SDK version if integrated by app, otherwise null.
     */
    @JvmStatic
    fun getRmcVersion(context: Context): String? {
        return try {
            context.getString(context.resources.getIdentifier(
                "rmc_inappmessaging__version",
                "string",
                context.packageName
            )) + RMC_SUFFIX
        } catch (e: Exception) {
            InAppLogger(TAG).debug(e.message)
            return null
        }
    }
}
