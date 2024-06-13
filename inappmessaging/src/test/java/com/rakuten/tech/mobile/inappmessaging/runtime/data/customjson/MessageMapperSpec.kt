package com.rakuten.tech.mobile.inappmessaging.runtime.data.customjson

import com.rakuten.tech.mobile.inappmessaging.runtime.data.enums.ButtonActionType
import com.rakuten.tech.mobile.inappmessaging.runtime.data.responses.ping.MessageButton
import com.rakuten.tech.mobile.inappmessaging.runtime.data.responses.ping.OnClickBehavior
import com.rakuten.tech.mobile.inappmessaging.runtime.testhelpers.TestDataHelper
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

@SuppressWarnings("LongMethod")
class MessageMapperSpec {
    private val message = TestDataHelper.createDummyMessage()

    @Test
    fun `should do nothing if there is no customJson`() {
        val uiMessage = MessageMapper.mapFrom(message)

        MessageMapper.mapFrom(message) shouldBeEqualTo uiMessage
    }

    @Test
    fun `should do nothing if customJson pushPrimer does not exist`() {
        val message = message.copy(customJson = CustomJson(pushPrimer = null))
        val uiMessage = MessageMapper.mapFrom(message)

        MessageMapper.mapFrom(message) shouldBeEqualTo uiMessage
    }

    @Test
    fun `should apply custom PushPrimer setting`() {
        val payload = TestDataHelper.message0Payload.copy(
            messageSettings = TestDataHelper.message0Payload.messageSettings.copy(
                controlSettings = TestDataHelper.message0Payload.messageSettings.controlSettings.copy(
                    buttons = listOf(
                        MessageButton(
                            "#FF0000", "#FF0000",
                            OnClickBehavior(ButtonActionType.DEEPLINK.typeId, ""), "text", null,
                        ),
                    ),
                ),
            ),
        )
        val uiMessage = MessageMapper.mapFrom(
            message.copy(
                messagePayload = payload,
                customJson = CustomJson(pushPrimer = PushPrimer(buttons = listOf("1"))),
            ),
        )

        uiMessage.buttons.size shouldBeEqualTo 1
        uiMessage.buttons[0].buttonBehavior.action shouldBeEqualTo ButtonActionType.PUSH_PRIMER.typeId
    }
}
