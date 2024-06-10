package com.rakuten.tech.mobile.inappmessaging.runtime.data.customjson

import com.rakuten.tech.mobile.inappmessaging.runtime.data.enums.ButtonActionType
import com.rakuten.tech.mobile.inappmessaging.runtime.data.responses.ping.MessageButton
import com.rakuten.tech.mobile.inappmessaging.runtime.data.ui.UiMessage

internal fun UiMessage.applyCustomPushPrimer(pushPrimer: PushPrimer): UiMessage {
    if (pushPrimer.buttons.isNullOrEmpty()) {
        return this
    }

    val customButtons = mutableListOf<MessageButton>()
    // buttons: ["1", "2"]
    for ((index, rawButton) in buttons.withIndex()) {
        // Change action to PushPrimer
        val shouldUpdateActionToPPrimer = pushPrimer.buttons.contains("${index+1}")
        if (shouldUpdateActionToPPrimer) {
            val customButton = rawButton.copy(
                buttonBehavior = rawButton.buttonBehavior.copy(
                    action = ButtonActionType.PUSH_PRIMER.typeId
                )
            )
            customButtons.add(customButton)
        }
    }

    return this.copy(
        buttons = customButtons
    )
}