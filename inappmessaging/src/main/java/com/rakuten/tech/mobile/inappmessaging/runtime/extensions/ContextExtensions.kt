package com.rakuten.tech.mobile.inappmessaging.runtime.extensions

import android.content.Context
import android.content.Intent
import android.provider.Settings

fun Context.openAppNotifSettings() {
//    startActivity(Intent().apply {
////        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//        data = Uri.fromParts("package", packageName, null)
//    })

    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
    startActivity(intent)
}
