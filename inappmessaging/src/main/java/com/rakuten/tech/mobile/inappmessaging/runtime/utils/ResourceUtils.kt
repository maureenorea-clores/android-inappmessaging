package com.rakuten.tech.mobile.inappmessaging.runtime.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.Window
import androidx.core.content.res.ResourcesCompat

internal object ResourceUtils {
    internal var mockFont: Typeface? = null

    fun getResourceIdentifier(context: Context, name: String, type: String) =
        context.resources.getIdentifier(name, type, context.packageName)

    @SuppressLint("NewApi")
    fun getFont(context: Context, id: Int) = when {
        id <= 0 -> null
        BuildVersionChecker.instance().isAndroidOAndAbove() -> context.resources.getFont(id)
        else -> mockFont ?: ResourcesCompat.getFont(context, id)
    }

    private fun <T : View> findViewByName(activity: Activity, name: String): T? {
        val id = getResourceIdentifier(activity, name, "id")
        if (id > 0) {
            return activity.findViewById(id)
        }
        return null
    }

    fun findView(activity: Activity, name: String): View? {

//        var view: View? = null
//        if (activity != null)
//            view = findViewByName(activity, name)
//
//        if (view == null && parent != null) {
//            view = parent.findViewWithTag(name)
//
//            if (view == null) {
//                val views = arrayListOf<View>()
//                parent.findViewsWithText(views, name, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION)
//                view = views.firstOrNull()
//            }
//        }
//
//        return view

        var view = findViewByName<View>(activity, name)
        if (view == null) {
            val contentView: View? = activity.findViewById(Window.ID_ANDROID_CONTENT)
            contentView?.let {
                view = contentView.findViewWithTag(name)
                if (view == null) {
                    val views = arrayListOf<View>()
                    contentView.findViewsWithText(views, name, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION)
                    view = views.firstOrNull()
                }
            }
        }
        return view
    }
}
