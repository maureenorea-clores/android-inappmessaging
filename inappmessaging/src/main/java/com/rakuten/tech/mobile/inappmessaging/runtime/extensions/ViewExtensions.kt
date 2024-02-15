package com.rakuten.tech.mobile.inappmessaging.runtime.extensions

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager

internal fun View.isVisible(outPosition: Rect? = null): Boolean {
    if (!isShown) {
        return false
    }

//    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
//    val displayMetrics = DisplayMetrics()
//    wm.defaultDisplay.getRealMetrics(displayMetrics)

    val actualPosition = outPosition ?: Rect()
//    val screen = Rect(
//        0, 0,
//        Resources.getSystem().displayMetrics.widthPixels, Resources.getSystem().displayMetrics.heightPixels,
//    )
    val windowVisibleFrame = Rect()
    getWindowVisibleDisplayFrame(windowVisibleFrame)
    val screen = Rect(0, 0, windowVisibleFrame.right, windowVisibleFrame.bottom)

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
