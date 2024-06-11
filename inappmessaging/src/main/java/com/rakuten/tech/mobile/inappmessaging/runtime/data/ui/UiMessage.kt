package com.rakuten.tech.mobile.inappmessaging.runtime.data.ui

import com.rakuten.tech.mobile.inappmessaging.runtime.data.models.Tooltip
import com.rakuten.tech.mobile.inappmessaging.runtime.data.responses.ping.Content
import com.rakuten.tech.mobile.inappmessaging.runtime.data.responses.ping.DisplaySettings
import com.rakuten.tech.mobile.inappmessaging.runtime.data.responses.ping.MessageButton

internal data class UiMessage(
    val id: String,
    val type: Int,
    val isTest: Boolean,
    val backgroundColor: String,
    val headerText: String?,
    val headerColor: String,
    val bodyText: String?,
    val bodyColor: String,
    val imageUrl: String?,
    val showTopCloseButton: Boolean,
    var buttons: List<MessageButton>,
    val displaySettings: DisplaySettings,
    val content: Content?,
    val tooltipData: Tooltip?
)