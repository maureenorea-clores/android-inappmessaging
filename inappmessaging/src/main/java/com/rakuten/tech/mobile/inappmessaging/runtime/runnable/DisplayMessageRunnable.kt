package com.rakuten.tech.mobile.inappmessaging.runtime.runnable

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.UiThread
import com.rakuten.tech.mobile.inappmessaging.runtime.R
import com.rakuten.tech.mobile.inappmessaging.runtime.data.enums.ImpressionType
import com.rakuten.tech.mobile.inappmessaging.runtime.data.enums.InAppMessageType
import com.rakuten.tech.mobile.inappmessaging.runtime.data.models.Tooltip
import com.rakuten.tech.mobile.inappmessaging.runtime.data.repositories.CampaignRepository
import com.rakuten.tech.mobile.inappmessaging.runtime.data.responses.ping.Message
import com.rakuten.tech.mobile.inappmessaging.runtime.data.requests.Impression
import com.rakuten.tech.mobile.inappmessaging.runtime.manager.DisplayManager
import com.rakuten.tech.mobile.inappmessaging.runtime.manager.ImpressionManager
import com.rakuten.tech.mobile.inappmessaging.runtime.utils.InAppLogger
import com.rakuten.tech.mobile.inappmessaging.runtime.utils.ResourceUtils
import com.rakuten.tech.mobile.inappmessaging.runtime.utils.ViewUtil
import com.rakuten.tech.mobile.inappmessaging.runtime.view.InAppMessageFullScreenView
import com.rakuten.tech.mobile.inappmessaging.runtime.view.InAppMessageModalView
import com.rakuten.tech.mobile.inappmessaging.runtime.view.InAppMessageSlideUpView
import com.rakuten.tech.mobile.inappmessaging.runtime.view.InAppMessagingTooltipView
import kotlinx.coroutines.Runnable
import java.util.Date

/**
 * Displaying message runnable which presents the message on the UI thread. Message close, and other
 * button actions will also be handled here.
 */
@UiThread
internal class DisplayMessageRunnable(
    private val message: Message,
    private val hostActivity: Activity,
    private val displayManager: DisplayManager = DisplayManager.instance(),
) : Runnable {
    internal var testLayout: FrameLayout? = null

    /**
     * Interface method which will be invoked by the Virtual Machine. This is also the actual method
     * which will display message with correct data.
     */
    @UiThread
    override fun run() {
        val messageType = InAppMessageType.getById(message.type)
        if (shouldNotDisplay(messageType)) return

        if (messageType != null) {
            when (messageType) {
                InAppMessageType.MODAL -> handleModal()
                InAppMessageType.FULL -> handleFull()
                InAppMessageType.SLIDE -> handleSlide()
                InAppMessageType.HTML, InAppMessageType.INVALID -> Any()
                InAppMessageType.TOOLTIP -> handleTooltip()
            }
        }
    }

    private fun handleSlide() {
        val slideUpView = hostActivity.layoutInflater.inflate(R.layout.in_app_message_slide_up, null)
            as InAppMessageSlideUpView
        slideUpView.populateViewData(message)
        hostActivity.addContentViewWithChecks(slideUpView, hostActivity.window.attributes)
    }

    private fun handleFull() {
        val fullScreenView = hostActivity.layoutInflater.inflate(R.layout.in_app_message_full_screen, null)
            as InAppMessageFullScreenView
        fullScreenView.populateViewData(message)
        hostActivity.addContentViewWithChecks(fullScreenView, hostActivity.window.attributes)
    }

    private fun handleModal() {
        val modalView = hostActivity.layoutInflater.inflate(R.layout.in_app_message_modal, null)
            as InAppMessageModalView
        modalView.populateViewData(message)
        hostActivity.addContentViewWithChecks(modalView, hostActivity.window.attributes)
    }

    private fun handleTooltip() {
        val toolTipView = hostActivity.layoutInflater.inflate(R.layout.in_app_message_tooltip, null)
            as InAppMessagingTooltipView
        toolTipView.populateViewData(message)
        message.getTooltipConfig()?.let { config ->
            if (displayTooltip(config, toolTipView)) {
                ImpressionManager.sendImpressionEvent(
                    message.campaignId,
                    listOf(Impression(ImpressionType.IMPRESSION, Date().time)),
                    impressionTypeOnly = true,
                )
            }
        }
    }

    private fun displayTooltip(tooltip: Tooltip, toolTipView: InAppMessagingTooltipView): Boolean {
        var isTooltipAdded = false
        ResourceUtils.findViewByName<View>(hostActivity, tooltip.id)?.let { target ->
            val scroll = ViewUtil.getScrollView(target)
            isTooltipAdded = if (scroll != null) {
                displayInScrollView(scroll, toolTipView)
            } else {
                val params = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                )
                return hostActivity.addContentViewWithChecks(toolTipView, params)
            }
            if (tooltip.autoDisappear != null && tooltip.autoDisappear > 0) {
                displayManager.removeMessage(hostActivity, delay = tooltip.autoDisappear, id = message.campaignId)
            }
        }
        return isTooltipAdded
    }

    /** Adds the tooltip to the anchor view's parent scroll view. */
    private fun displayInScrollView(scroll: ViewGroup, toolTipView: InAppMessagingTooltipView): Boolean {
        val anchor = scroll.getChildAt(0) as? ViewGroup
        anchor?.addView(
            toolTipView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
            ),
        )
        return anchor != null
    }

    private fun shouldNotDisplay(messageType: InAppMessageType?): Boolean {
        val normalCampaign = hostActivity.findViewById<View?>(R.id.in_app_message_base_view)
        return if (messageType == InAppMessageType.TOOLTIP) {
            // if normal non-slide-up campaign is displayed, don't display tooltip on top of normal campaign
            if (normalCampaign != null && normalCampaign !is InAppMessageSlideUpView) {
                true
            } else {
                checkTooltipDisplay()
            }
        } else {
            normalCampaign != null
        }
    }

    private fun checkTooltipDisplay(): Boolean {
        hostActivity.findViewById<View?>(R.id.in_app_message_tooltip_view)?.parent?.let { viewParent ->
            for (i in 0 until (viewParent as ViewGroup).childCount) {
                val child = viewParent.getChildAt(i)
                if (child?.id == R.id.in_app_message_tooltip_view && child.tag == message.campaignId) {
                    // tool campaign is already displayed, no need to display again
                    return true
                }
            }
        }
        return false
    }

    /**
     * Performs any final checks before actually adding the [view] to the [hostActivity].
     * When added, will add impression.
     */
    private fun Activity.addContentViewWithChecks(view: View, layoutParams: ViewGroup.LayoutParams): Boolean {
        // There may be cases where user suddenly changed and this message is now obsolete
        if (!CampaignRepository.instance().isSyncedWithCurrentProvider()) {
            InAppLogger("IAM_DisplayMessageRunnable").debug(
                "Cancelled displaying campaign: ${message.campaignId}")
            return false
        }

        this.addContentView(view, layoutParams)
        ImpressionManager.sendImpressionEvent(
            message.campaignId,
            listOf(Impression(ImpressionType.IMPRESSION, Date().time)),
            impressionTypeOnly = true,
        )
        return true
    }
}
