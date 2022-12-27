package com.rakuten.tech.mobile.inappmessaging.runtime.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.drawable.ScaleDrawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.VisibleForTesting
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.rakuten.tech.mobile.inappmessaging.runtime.InAppMessaging
import com.rakuten.tech.mobile.inappmessaging.runtime.R
import com.rakuten.tech.mobile.inappmessaging.runtime.data.enums.PositionType
import com.rakuten.tech.mobile.inappmessaging.runtime.data.models.messages.Message
import com.rakuten.tech.mobile.inappmessaging.runtime.utils.InAppLogger
import com.rakuten.tech.mobile.inappmessaging.runtime.utils.ResourceUtils
import com.rakuten.tech.mobile.inappmessaging.runtime.utils.ViewUtil
import com.rakuten.tech.mobile.inappmessaging.runtime.utils.show
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

@SuppressWarnings("LargeClass", "LongMethod")
internal class InAppMessagingTooltipView(
    context: Context,
    attrs: AttributeSet?
) : RelativeLayout(context, attrs), InAppMessageView {

    init {
        id = R.id.in_app_message_tooltip_view
    }

    private var imageUrl: String? = null
    private var bgColor = "#FFFFFF" // default white
    internal var type: PositionType = PositionType.BOTTOM_CENTER
    private var viewId: String? = null
    private var listener: InAppMessageViewListener? = null
    internal var isTest = false
    internal var mainHandler = Handler(Looper.getMainLooper())
    private val anchorViewLayoutListener = ViewTreeObserver.OnGlobalLayoutListener { setPosition() }

    @VisibleForTesting
    internal var picasso: Picasso? = null

    override fun populateViewData(message: Message) {
        // set tag
        tag = message.getCampaignId()
        this.imageUrl = message.getMessagePayload().resource.imageUrl
        this.bgColor = message.getMessagePayload().backgroundColor
        message.getTooltipConfig()?.let { tooltip ->
            val position = PositionType.getById(tooltip.position)
            if (position != null) {
                type = position
            }
            viewId = tooltip.id
        }
        listener = InAppMessageViewListener(message)
        bindImage()
    }

    /**
     * Removes listeners attached to the anchor view.
     */
    fun removeAnchorViewListeners() {
        try {
            findAnchorView()?.viewTreeObserver?.removeOnGlobalLayoutListener(anchorViewLayoutListener)
        } catch (e: IllegalStateException) {
            InAppLogger(TAG).debug(e, "Anchor viewTreeObserver is not alive anymore")
        }
    }

    /**
     * This method binds image to view.
     */
    @Suppress("ClickableViewAccessibility", "TooGenericExceptionCaught", "LongMethod")
    private fun bindImage() { // Display image.
        if (!this.imageUrl.isNullOrEmpty()) {
            // load the image then display the view
            this.show(false)
            findViewById<ImageView>(R.id.message_tooltip_image_view).let { imageView ->
                try {
                    val callback = object : Callback {
                        override fun onSuccess() {
                            imageView.show(false)
                            this@InAppMessagingTooltipView.show(false)
                        }

                        override fun onError(e: Exception?) {
                            InAppLogger(TAG).debug(e?.cause, "Downloading image failed $imageUrl")
                        }
                    }

                    imageView.viewTreeObserver.addOnGlobalLayoutListener(
                        object : ViewTreeObserver.OnGlobalLayoutListener {
                            override fun onGlobalLayout() {
                                if (imageView.width > 0 || isTest) {
                                    imageView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                    drawBorder(imageView.width, imageView.height)
                                    drawTip()
                                    addAnchorViewListeners()
                                    // to avoid flicker
                                    mainHandler.postDelayed({
                                        imageView.show()
                                        this@InAppMessagingTooltipView.show()
                                    }, DELAY)
                                }
                            }
                        })
                    (picasso ?: Picasso.get()).load(this.imageUrl)
                        .priority(Picasso.Priority.HIGH)
                        .resize(MAX_SIZE, MAX_SIZE)
                        .onlyScaleDown()
                        .centerInside()
                        .into(imageView, callback)
                } catch (ex: Exception) {
                    InAppLogger(TAG).debug(ex, "Downloading image failed $imageUrl")
                }
            }
        }
    }

    private fun drawBorder(width: Int, height: Int) {
        val imageView = findViewById<ShapeableImageView>(R.id.message_tooltip_image_view)
        imageView.layoutParams.width = width + PADDING
        imageView.layoutParams.height = height + PADDING

        val shapePathModel = ShapeAppearanceModel.builder()
            .setAllCorners(CornerFamily.ROUNDED, RADIUS)
            .build()

        val backgroundDrawable = MaterialShapeDrawable(shapePathModel).apply {
            fillColor = ColorStateList.valueOf(Color.parseColor(bgColor))
        }

        imageView.background = backgroundDrawable
    }

    @SuppressWarnings("MagicNumber", "ComplexMethod")
    private fun drawTip() {
        val tip = findViewById<ImageView>(R.id.message_tip)
        tip.layoutParams.height = TRI_SIZE
        tip.layoutParams.width = TRI_SIZE

        var adjustedWidth = TRI_SIZE
        var adjustedHeight = TRI_SIZE
        val close = findViewById<ImageButton>(R.id.message_close_button)
        if (close.drawable is ScaleDrawable) {
            close.drawable.level = 1
        }
        val imageView = findViewById<ShapeableImageView>(R.id.message_tooltip_image_view)
        tip.setOnClickListener(this.listener)
        close.setOnClickListener(this.listener)
        imageView.setOnClickListener(this.listener)
        val ptArray = when (type) {
            PositionType.TOP_RIGHT -> {
                adjustedHeight = PADDING
                adjustedWidth = PADDING
                tip.layoutParams.height = PADDING
                tip.layoutParams.width = PADDING
                (tip.layoutParams as LayoutParams).addRule(ALIGN_START, R.id.message_tooltip_image_view)
                (tip.layoutParams as LayoutParams).addRule(ALIGN_BOTTOM, R.id.message_tooltip_image_view)
                (tip.layoutParams as MarginLayoutParams).leftMargin = -PADDING / 2
                (tip.layoutParams as MarginLayoutParams).bottomMargin = -PADDING / 2
                arrayOf(
                    Point(PADDING * 2 / 3, 0),
                    Point(0, PADDING),
                    Point(PADDING, PADDING * 1 / 3)
                )
            }
            PositionType.TOP_CENTER -> {
                (tip.layoutParams as LayoutParams).addRule(ALIGN_START, R.id.message_tooltip_image_view)
                (tip.layoutParams as LayoutParams).addRule(ALIGN_END, R.id.message_tooltip_image_view)
                (tip.layoutParams as LayoutParams).addRule(CENTER_HORIZONTAL)
                (tip.layoutParams as LayoutParams).addRule(BELOW, R.id.message_tooltip_image_view)

                arrayOf(
                    Point(0, 0),
                    Point(TRI_SIZE / 2, TRI_SIZE),
                    Point(TRI_SIZE, 0)
                )
            }
            PositionType.TOP_LEFT -> {
                alignLeft(close, tip)
                (tip.layoutParams as LayoutParams).addRule(ALIGN_BOTTOM, R.id.message_tooltip_image_view)
                (tip.layoutParams as MarginLayoutParams).rightMargin = -PADDING / 2
                (tip.layoutParams as MarginLayoutParams).bottomMargin = -PADDING / 2
                adjustedHeight = PADDING
                adjustedWidth = PADDING
                arrayOf(
                    Point(PADDING * 1 / 3, 0),
                    Point(PADDING, PADDING),
                    Point(0, PADDING * 1 / 3)
                )
            }
            PositionType.BOTTOM_RIGHT -> {
                adjustedHeight = PADDING
                adjustedWidth = PADDING
                tip.layoutParams.height = PADDING
                tip.layoutParams.width = PADDING
                (tip.layoutParams as LayoutParams).addRule(ALIGN_TOP, R.id.message_tooltip_image_view)
                (tip.layoutParams as MarginLayoutParams).leftMargin = -PADDING / 2
                (tip.layoutParams as MarginLayoutParams).topMargin = -PADDING / 2
                arrayOf(
                    Point(PADDING * 2 / 3, PADDING),
                    Point(0, 0),
                    Point(PADDING, PADDING * 2 / 3)
                )
            }
            PositionType.BOTTOM_CENTER -> {
                (findViewById<RelativeLayout>(R.id.image_layout).layoutParams as LayoutParams).removeRule(BELOW)
                (close.layoutParams as LayoutParams).addRule(BELOW, R.id.image_layout)
                (tip.layoutParams as LayoutParams).addRule(ALIGN_START, R.id.message_tooltip_image_view)
                (tip.layoutParams as LayoutParams).addRule(ALIGN_END, R.id.message_tooltip_image_view)
                (tip.layoutParams as LayoutParams).addRule(CENTER_HORIZONTAL)
                (imageView.layoutParams as LayoutParams).addRule(BELOW, R.id.message_tip)
                arrayOf(
                    Point(0, TRI_SIZE),
                    Point(TRI_SIZE / 2, 0),
                    Point(TRI_SIZE, TRI_SIZE)
                )
            }
            PositionType.BOTTOM_LEFT -> {
                alignLeft(close, tip)
                adjustedHeight = PADDING
                adjustedWidth = PADDING
                (tip.layoutParams as LayoutParams).addRule(ALIGN_TOP, R.id.message_tooltip_image_view)
                (tip.layoutParams as MarginLayoutParams).rightMargin = -PADDING / 2
                (tip.layoutParams as MarginLayoutParams).topMargin = -PADDING / 2
                arrayOf(
                    Point(0, PADDING * 2 / 3),
                    Point(PADDING, 0),
                    Point(PADDING * 1 / 3, PADDING)
                )
            }
            PositionType.RIGHT -> {
                (tip.layoutParams as LayoutParams).addRule(CENTER_VERTICAL)
                (imageView.layoutParams as LayoutParams).addRule(RIGHT_OF, R.id.message_tip)
                arrayOf(
                    Point(TRI_SIZE, 0),
                    Point(0, TRI_SIZE / 2),
                    Point(TRI_SIZE, TRI_SIZE)
                )
            }
            PositionType.LEFT -> {
                (close.layoutParams as LayoutParams).removeRule(END_OF)
                (close.layoutParams as LayoutParams).removeRule(ABOVE)
                (close.layoutParams as LayoutParams).removeRule(RIGHT_OF)
                (findViewById<RelativeLayout>(R.id.image_layout).layoutParams as LayoutParams)
                    .addRule(END_OF, R.id.message_close_button)
                (tip.layoutParams as LayoutParams).addRule(CENTER_VERTICAL)
                (tip.layoutParams as LayoutParams).addRule(RIGHT_OF, R.id.message_tooltip_image_view)
                arrayOf(
                    Point(0, 0),
                    Point(TRI_SIZE, TRI_SIZE / 2),
                    Point(0, TRI_SIZE)
                )
            }
        }

        val path = Path()
        path.fillType = Path.FillType.EVEN_ODD
        path.moveTo(ptArray[0].x.toFloat(), ptArray[0].y.toFloat())
        path.lineTo(ptArray[1].x.toFloat(), ptArray[1].y.toFloat())
        path.lineTo(ptArray[2].x.toFloat(), ptArray[2].y.toFloat())
        path.close()
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        paint.strokeWidth = 2f
        paint.color = Color.parseColor(bgColor)
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.isAntiAlias = true
        val bg = Bitmap.createBitmap(adjustedWidth, adjustedHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bg)
        canvas.drawPath(path, paint)

        tip.setImageBitmap(bg)

        // so that the arrow head of tooltip positioned at anchor's edge not be clipped
        (parent as? ViewGroup)?.clipChildren = false
        (parent as? ViewGroup)?.clipToPadding = false
    }

    private fun alignLeft(close: ImageButton, tip: ImageView) {
        (close.layoutParams as LayoutParams).removeRule(END_OF)
        (close.layoutParams as LayoutParams).removeRule(ABOVE)
        (close.layoutParams as LayoutParams).removeRule(RIGHT_OF)
        (findViewById<RelativeLayout>(R.id.image_layout).layoutParams as LayoutParams).addRule(
            END_OF,
            R.id.message_close_button
        )
        tip.layoutParams.height = PADDING
        tip.layoutParams.width = PADDING
        (tip.layoutParams as LayoutParams).addRule(ALIGN_END, R.id.message_tooltip_image_view)
    }

    private fun findAnchorView(): View? {
        val activity = InAppMessaging.instance().getRegisteredActivity()
        if (activity != null) {
            viewId?.let { return ResourceUtils.findViewByName(activity, it) }
        }
        return null
    }

    private fun addAnchorViewListeners() {
        try {
            findAnchorView()?.viewTreeObserver?.addOnGlobalLayoutListener(anchorViewLayoutListener)
        } catch (e: IllegalStateException) {
            InAppLogger(TAG).debug(e, "Anchor viewTreeObserver is not alive anymore")
        }
    }

    /**
     * Sets the top-left position of this tooltip view.
     */
    private fun setPosition() {
        val activity = InAppMessaging.instance().getRegisteredActivity() ?: return
        findAnchorView()?.let { anchorView ->
            val container = ViewUtil.getScrollView(anchorView) ?: activity.findViewById(android.R.id.content)
            val tPosition = ViewUtil.getTooltipPosition(
                container = container,
                view = this,
                anchorView = anchorView,
                positionType = type,
                margin = findViewById<ImageButton>(R.id.message_close_button).height
            )
            this.x = tPosition.x.toFloat()
            this.y = tPosition.y.toFloat()
        }
    }

    companion object {
        private const val TAG = "IAM_ToolTipView"
        internal const val PADDING = 40
        internal const val TRI_SIZE = 20
        private const val RADIUS = 15f
        private const val MAX_SIZE = 600
        private const val DELAY = 100L // in ms
    }
}
