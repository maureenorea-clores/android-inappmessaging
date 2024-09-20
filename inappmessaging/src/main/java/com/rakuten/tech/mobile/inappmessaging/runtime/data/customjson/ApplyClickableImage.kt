package com.rakuten.tech.mobile.inappmessaging.runtime.data.customjson

import com.rakuten.tech.mobile.inappmessaging.runtime.data.enums.ButtonActionType
import com.rakuten.tech.mobile.inappmessaging.runtime.data.enums.InAppMessageType
import com.rakuten.tech.mobile.inappmessaging.runtime.data.responses.ping.Content
import com.rakuten.tech.mobile.inappmessaging.runtime.data.responses.ping.OnClickBehavior
import com.rakuten.tech.mobile.inappmessaging.runtime.data.ui.UiMessage

internal fun UiMessage.applyCustomClickableImage(clickableImage: ClickableImage?, isPushPrimer: Boolean): UiMessage {
    fun Int.campaignTypeCanBeClickable(): Boolean {
        return this in listOf(
            InAppMessageType.FULL.typeId,
            InAppMessageType.MODAL.typeId,
        )
    }

    fun String?.isValidUrlOrDeeplink(): Boolean {
        if (this.isNullOrBlank() || this != this.trim()) {
            return false
        }

        return if (this.startsWith("http")) {
            Regex("https://.*").matches(this)
        } else {
            Regex(".*://.*").matches(this)
        }
    }

    @SuppressWarnings("ComplexCondition")
    if (clickableImage == null ||
        !clickableImage.url.isValidUrlOrDeeplink() ||
        imageUrl.isNullOrEmpty() ||
        !type.campaignTypeCanBeClickable() ||
        isPushPrimer
    ) {
        return this
    }

    val newOnclick = OnClickBehavior(action = ButtonActionType.REDIRECT.typeId, uri = clickableImage.url)
    return this.copy(
        content = if (this.content == null) {
            Content(onClick = newOnclick)
        } else {
            this.content.copy(onClick = newOnclick)
        },
    )
}
