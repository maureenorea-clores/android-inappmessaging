package com.rakuten.tech.mobile.inappmessaging.runtime.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.rakuten.tech.mobile.inappmessaging.runtime.extensions._Tooltips
import com.rakuten.tech.mobile.inappmessaging.runtime.view.Tooltips

internal object ResourceUtils {
    internal var mockFont: Typeface? = null

    fun getResourceIdentifier(context: Context, name: String, type: String) =
        context.resources.getIdentifier(name, type, context.packageName)

    @SuppressLint("NewApi")
    fun getFont(context: Context, id: Int) = when {
        id <= 0 -> null
        BuildVersionChecker.isAndroidOAndAbove() -> context.resources.getFont(id)
        else -> mockFont ?: ResourcesCompat.getFont(context, id)
    }

    fun <T : View> findViewByName(activity: Activity, name: String): View? {
//        var id = getResourceIdentifier(activity, name, "id")
//
//        if (id <= 0) {
//            id = _Tooltips.findIdByName(name) ?: -1
//        }
//
//        return activity.findViewById(id)

        var id = getResourceIdentifier(activity, name, "id")

        var v: View? = null

        if (id > 0) {
            v = activity.findViewById(id)
        }

        if (v == null) {
            v = Tooltips.getView(name)
        }

        return v
    }
}
