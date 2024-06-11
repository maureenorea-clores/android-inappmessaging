package com.rakuten.tech.mobile.inappmessaging.runtime.data.customjson

import com.rakuten.tech.mobile.inappmessaging.runtime.data.enums.ButtonActionType
import com.rakuten.tech.mobile.inappmessaging.runtime.data.responses.ping.MessageButton
import com.rakuten.tech.mobile.inappmessaging.runtime.data.ui.UiMessage

internal fun UiMessage.applyCustomPushPrimer(pushPrimer: PushPrimer) {
    if (pushPrimer.buttons.isNullOrEmpty()) {
        return
    }

    val customButtons = mutableListOf<MessageButton>()
    for ((index, rawButton) in buttons.withIndex()) {
        val shouldUpdateActionToPPrimer = pushPrimer.buttons.contains("${index+1}")
        val customButton = if (!shouldUpdateActionToPPrimer) {
            rawButton
        } else {
            rawButton.copy(
                buttonBehavior = rawButton.buttonBehavior.copy(
                    action = ButtonActionType.PUSH_PRIMER.typeId
                )
            )
        }

        customButtons.add(customButton)
    }

    this.apply {
        buttons = customButtons
    }
}