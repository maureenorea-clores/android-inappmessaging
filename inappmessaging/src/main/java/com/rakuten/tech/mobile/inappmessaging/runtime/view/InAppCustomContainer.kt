package com.rakuten.tech.mobile.inappmessaging.runtime.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.widget.NestedScrollView

/**
 * NestedScrollView is used so that view holders are bounded on demand.
 * https://stackoverflow.com/questions/38130686/how-to-force-recyclerview-adapter-to-call-the-onbindviewholder-on-all-elements
 */
class InAppCustomContainer(context: Context, attrs:  AttributeSet?): NestedScrollView(context, attrs) {

    override fun onFinishInflate() {
        super.onFinishInflate()
        initializeView()
    }

    private fun initializeView() {

        val frameLayout = FrameLayout(context)

        try {
//            this.addView(frameLayout, ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            ))
            this.addView(frameLayout)
        } catch (e: Exception) {
            println("Failed to initializeView: $e")
        }
    }
}