package com.rakuten.tech.mobile.inappmessaging.runtime.extensions

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat

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

// TODO: Check Java compatibility
fun View.canHaveTooltip(
    identifier: String
) {
    if (this.id == View.NO_ID) {
        this.id = ViewCompat.generateViewId()
    }

    _Tooltips.addId(this.id, identifier)
}

internal object _Tooltips {
    private val tooltips = hashMapOf<String, Int>()

    fun addId(viewId: Int, viewName: String) {
        tooltips[viewName] = viewId
    }

    fun findIdByName(viewName: String): Int? {
        return tooltips[viewName]
    }
}