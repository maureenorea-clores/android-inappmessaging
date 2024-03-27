package com.rakuten.tech.mobile.inappmessaging.runtime.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.view.View
import androidx.core.content.res.ResourcesCompat
import java.lang.ref.WeakReference

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

    private fun <T : View> findViewByName(activity: Activity, name: String): T? {
        val id = getResourceIdentifier(activity, name, "id")
        if (id > 0) {
            return activity.findViewById(id)
        }
        return null
    }

    /**
     * Returns the first view found based on the following identifier, in order of precedence:
     *
     * android:id - the name of the resource identifier e.g. R.id.myView. [identifier] is "myView".
     * android:tag - any arbitrary String value associated with the view.
     * android:contentDescription - property used in assistive apps.
     */
    fun findViewByIdentifier(activity: Activity, identifier: String): WeakReference<View>? {
        val viewById = findViewByName<View>(activity, identifier)
        if (viewById != null) return WeakReference(viewById)

        val contentView = activity.findViewById<View>(android.R.id.content)
        val viewByTag: View? = contentView?.findViewWithTag(identifier)
        if (viewByTag != null) return WeakReference(viewByTag)

        val views = arrayListOf<View>()
        contentView?.findViewsWithText(views, identifier, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION)
        if (views.firstOrNull() != null) return WeakReference(views.first())

        return null
    }
}
