package com.rakuten.tech.mobile.inappmessaging.runtime.extensions

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

internal fun View.isVisible(outPosition: Rect? = null): Boolean {
    if (!isShown) {
        return false
    }

    val actualPosition = outPosition ?: Rect()
    val screen = Rect(
        0, 0,
        Resources.getSystem().displayMetrics.widthPixels, Resources.getSystem().displayMetrics.heightPixels,
    )

    return getGlobalVisibleRect(actualPosition) && Rect.intersects(actualPosition, screen)
}

internal fun View.getRectLocationOnContainer(container: ViewGroup): Rect {
    val viewLoc = IntArray(2)
    this.getLocationOnScreen(viewLoc)

    val rootLayoutLoc = IntArray(2)
    container.getLocationOnScreen(rootLayoutLoc)

    val relativeLeft = viewLoc[0] - (rootLayoutLoc[0] - container.scrollX)
    val relativeTop = viewLoc[1] - (rootLayoutLoc[1] - container.scrollY)

    return Rect(relativeLeft, relativeTop, relativeLeft + width, relativeTop + height)
}

internal fun View.show() {
    visibility = View.VISIBLE
}

internal fun View.hide(asGone: Boolean = false) {
    visibility = if (asGone) View.GONE else View.INVISIBLE
}

internal fun View.findNearestScrollingParent(): ViewGroup? {
    var currView = this.parent
    while (currView != null) {
        if (currView is ScrollView ||
            currView is HorizontalScrollView ||
            currView is NestedScrollView ||
            currView is RecyclerView) {

            return WeakReference(currView as? ViewGroup).get()
        }
        currView = currView.parent
    }
    return null
}