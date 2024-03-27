package com.rakuten.tech.mobile.inappmessaging.runtime.runnable

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import androidx.annotation.UiThread
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.rakuten.tech.mobile.inappmessaging.runtime.R
import com.rakuten.tech.mobile.inappmessaging.runtime.data.enums.ImpressionType
import com.rakuten.tech.mobile.inappmessaging.runtime.data.enums.InAppMessageType
import com.rakuten.tech.mobile.inappmessaging.runtime.data.models.Tooltip
import com.rakuten.tech.mobile.inappmessaging.runtime.data.responses.ping.Message
import com.rakuten.tech.mobile.inappmessaging.runtime.data.requests.Impression
import com.rakuten.tech.mobile.inappmessaging.runtime.extensions.findNearestScrollingParent
import com.rakuten.tech.mobile.inappmessaging.runtime.extensions.isVisible
import com.rakuten.tech.mobile.inappmessaging.runtime.manager.DisplayManager
import com.rakuten.tech.mobile.inappmessaging.runtime.manager.ImpressionManager
import com.rakuten.tech.mobile.inappmessaging.runtime.utils.InAppLogger
import com.rakuten.tech.mobile.inappmessaging.runtime.utils.ResourceUtils
import com.rakuten.tech.mobile.inappmessaging.runtime.view.InAppMessageFullScreenView
import com.rakuten.tech.mobile.inappmessaging.runtime.view.InAppMessageModalView
import com.rakuten.tech.mobile.inappmessaging.runtime.view.InAppMessageSlideUpView
import com.rakuten.tech.mobile.inappmessaging.runtime.view.InAppMessagingTooltipView
import kotlinx.coroutines.Runnable
import java.lang.ref.WeakReference
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
    private val rootContainer = WeakReference(hostActivity.findViewById<ViewGroup>(android.R.id.content)).get()

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
        hostActivity.addContentView(slideUpView, hostActivity.window.attributes)
        ImpressionManager.sendImpressionEvent(
            message.campaignId,
            listOf(Impression(ImpressionType.IMPRESSION, Date().time)),
            impressionTypeOnly = true,
        )
    }

    private fun handleFull() {
        val fullScreenView = hostActivity.layoutInflater.inflate(R.layout.in_app_message_full_screen, null)
            as InAppMessageFullScreenView
        fullScreenView.populateViewData(message)
        hostActivity.addContentView(fullScreenView, hostActivity.window.attributes)
        ImpressionManager.sendImpressionEvent(
            message.campaignId,
            listOf(Impression(ImpressionType.IMPRESSION, Date().time)),
            impressionTypeOnly = true,
        )
    }

    private fun handleModal() {
        val modalView = hostActivity.layoutInflater.inflate(R.layout.in_app_message_modal, null)
            as InAppMessageModalView
        modalView.populateViewData(message)
        hostActivity.addContentView(modalView, hostActivity.window.attributes)
        ImpressionManager.sendImpressionEvent(
            message.campaignId,
            listOf(Impression(ImpressionType.IMPRESSION, Date().time)),
            impressionTypeOnly = true,
        )
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

    private fun displayTooltip(tooltipData: Tooltip, toolTipView: InAppMessagingTooltipView): Boolean {
        try {
            // TODO: Test multiple tooltips
            val anchorView = ResourceUtils.findViewByIdentifier(hostActivity, tooltipData.id)?.get() ?: return false

            val scrollingParent = anchorView.findNearestScrollingParent()
            val container = findContainerForTooltip(scrollingParent) ?: return false

            setupObservers(tooltipData, anchorView, container, toolTipView)
            container.addView(toolTipView, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ))
        } catch (e: Exception) {
            InAppLogger("IAM_DisplayMessageRunnable").warn("Failed to attach tooltip: $e")
            return false
        }
        return true
    }

    private fun setupObservers(tooltipData: Tooltip, anchorView: View, container: ViewGroup, tooltipView: InAppMessagingTooltipView) {
        val updateTooltipDisplay: () -> Unit = {
            println("[Mau] tooltipActions")
            tooltipView.setPosition(anchorView, container) // TODO: maybe only needed for RecyclerView?
            autoDisappearIfNeeded(tooltipView, tooltipData.autoDisappear)
        }
        val anchorGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener(updateTooltipDisplay)
        val anchorScrollListener = ViewTreeObserver.OnScrollChangedListener(updateTooltipDisplay)

        tooltipView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                println("[Mau] tooltip added")
                onAttachStateChanged(true, this)
            }

            override fun onViewDetachedFromWindow(v: View) {
                println("[Mau] tooltip removed")
                onAttachStateChanged(false, this)
            }

            fun onAttachStateChanged(isTooltipAttached: Boolean, listener: View.OnAttachStateChangeListener) {
                anchorView.viewTreeObserver?.let { anchorObserver ->
                    if (!anchorObserver.isAlive)
                        return

                    if (isTooltipAttached) {
                        println("[Mau] observers added")
                        anchorObserver.addOnGlobalLayoutListener(anchorGlobalLayoutListener)
                        anchorObserver.addOnScrollChangedListener(anchorScrollListener)
                    } else {
                        println("[Mau] observers removed")
                        anchorObserver.removeOnGlobalLayoutListener(anchorGlobalLayoutListener)
                        anchorObserver.removeOnScrollChangedListener(anchorScrollListener)
                        tooltipView.removeOnAttachStateChangeListener(listener)
                    }
                }
            }
        })
    }

    private fun autoDisappearIfNeeded(view: InAppMessagingTooltipView, autoDisappear: Int?) {
        if (autoDisappear == null || !view.isVisible())
            return

        displayManager.removeMessage(hostActivity, delay = autoDisappear, id = message.campaignId)
    }

    /**
     * For anchor views that are within a scrolling view, the tooltip should look like it has been inserted in the same
     * scrolling view so it can scroll and have the correct elevation.
     *
     * ScrollViews - can only have one direct child, so attach it to the child and not the ScrollView itself.
     * RecyclerView -
     * Default - root FrameLayout
     */
    private fun findContainerForTooltip(scrollingParent: ViewGroup?): ViewGroup? {
        return when(scrollingParent) {
            is ScrollView,
            is HorizontalScrollView,
            is NestedScrollView -> scrollingParent.getChildAt(0) as? ViewGroup
            is RecyclerView -> scrollingParent.parent as? ViewGroup//scrollingParent.getChildAt(-1) as? ViewGroup//
            else -> rootContainer
        }
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
}
