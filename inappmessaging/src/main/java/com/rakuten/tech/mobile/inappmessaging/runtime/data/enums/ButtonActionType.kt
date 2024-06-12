package com.rakuten.tech.mobile.inappmessaging.runtime.data.enums

import com.rakuten.tech.mobile.inappmessaging.runtime.data.PushPrimer

/**
 * Representing all In-App message's button actions.
 */
@SuppressWarnings("MagicNumber")
internal enum class ButtonActionType(val typeId: Int) {
    INVALID(0),
    REDIRECT(1),
    DEEPLINK(2),
    CLOSE(3),
    PUSH_PRIMER(4),
    ;

    companion object {

        /**
         * Gets the button action type for a given [typeId].
         * If [typeId] argument is not any of the valid id, null will be returned.
         */
        fun getById(typeId: Int, impressionType: ImpressionType, customPushPrimer: PushPrimer?): ButtonActionType? {

            if (customPushPrimer != null && !customPushPrimer.buttons.isNullOrEmpty()) {
                val buttonPosition = when(impressionType) {
                    ImpressionType.ACTION_ONE -> 1
                    ImpressionType.ACTION_TWO -> 2
                    else -> -1
                }

                val shouldUpdateActionToPPrimer = customPushPrimer.buttons.contains("$buttonPosition")
                if (shouldUpdateActionToPPrimer) {
                    return PUSH_PRIMER
                }
            }

            for (type in values()) {
                if (type.typeId == typeId) {
                    return type
                }
            }
            return null
        }
    }
}
