package com.rakuten.tech.mobile.inappmessaging.runtime.data.ui

import com.rakuten.tech.mobile.inappmessaging.runtime.data.models.Tooltip
import com.rakuten.tech.mobile.inappmessaging.runtime.data.responses.ping.Content
import com.rakuten.tech.mobile.inappmessaging.runtime.data.responses.ping.DisplaySettings
import com.rakuten.tech.mobile.inappmessaging.runtime.data.responses.ping.MessageButton

internal data class UiMessage(
    val id: String, //
//    val name: String,
    val type: Int,
    val isTest: Boolean,
    val backgroundColor: String, //
    val headerText: String?, //
    val headerColor: String, //
    val bodyText: String?, //
    val bodyColor: String, //
    val imageUrl: String?, //
    val showOptOutCheckBox: Boolean, //
    val showTopCloseButton: Boolean, //
    val buttons: List<MessageButton>, //
    val displaySettings: DisplaySettings,
    val content: Content?,
//    val triggers: List<Trigger>?,
    val tooltipData: Tooltip?,
//    val maxImpressions: Int,
//    val areImpressionsInfinite: Boolean,
//    val contexts: List<String>,
//    var impressionsLeft: Int?,
//    var isOptedOut: Boolean?
)