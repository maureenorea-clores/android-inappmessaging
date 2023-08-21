package com.rakuten.tech.mobile.inappmessaging.runtime.view

import android.view.View

// TODO: Check compatibility with Java
fun View.canHaveTooltip(
    identifier: String
) {
    Tooltips.addTooltip(identifier, this)
}


object Tooltips {
    private val tooltips = mutableMapOf<String, View>()

    fun addTooltip(identifier: String, view: View) {
        tooltips[identifier] = view
    }

    fun getView(identifier: String): View? {
        return tooltips[identifier]
    }
}