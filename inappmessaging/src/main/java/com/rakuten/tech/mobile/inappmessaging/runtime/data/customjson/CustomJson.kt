package com.rakuten.tech.mobile.inappmessaging.runtime.data.customjson

internal data class CustomJson(
    val pushPrimer: PushPrimer? = null
)

internal data class PushPrimer(
    val buttons: List<String>? = null
)
