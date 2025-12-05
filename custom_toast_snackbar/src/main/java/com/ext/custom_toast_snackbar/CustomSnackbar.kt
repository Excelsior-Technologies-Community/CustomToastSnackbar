package com.ext.custom_toast_snackbar

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar

class CustomSnackbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var text: String = "Snackbar"
    private var textColor: Int = Color.WHITE
    private var icon: Drawable? = null
    private var iconSize: Float = 24f
    private var backgroundColor: Int = Color.parseColor("#323232")
    private var cornerRadius: Float = 12f
    private var iconPadding: Int = 8

    private var paddingStart = 10
    private var paddingTop = 10
    private var paddingEnd = 10
    private var paddingBottom = 10

    private var marginStart = 0
    private var marginTop = 0
    private var marginEnd = 0
    private var marginBottom = 8

    private var duration = Snackbar.LENGTH_SHORT
    private var autoShow = true

    private var targetParentView: View? = null
    private var snackbar: Snackbar? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 14f * resources.displayMetrics.scaledDensity
    }
    private val rectF = RectF()

    init {
        visibility = VISIBLE
        setWillNotDraw(false) // Enable drawing for preview

        context.obtainStyledAttributes(attrs, R.styleable.CustomSnackbar, defStyleAttr, 0)
            .use { a ->
                text = a.getString(R.styleable.CustomSnackbar_snackbarText) ?: "Snackbar"
                textColor = a.getColor(R.styleable.CustomSnackbar_snackbarTextColor, textColor)
                icon = a.getDrawable(R.styleable.CustomSnackbar_snackbarIcon)
                iconSize = a.getDimension(R.styleable.CustomSnackbar_snackbarIconSize, iconSize)
                backgroundColor =
                    a.getColor(R.styleable.CustomSnackbar_snackbarBackgroundColor, backgroundColor)
                cornerRadius =
                    a.getDimension(R.styleable.CustomSnackbar_snackbarCornerRadius, cornerRadius)
                iconPadding = a.getDimensionPixelSize(
                    R.styleable.CustomSnackbar_snackbarIconPadding,
                    iconPadding
                )

                paddingStart = a.getDimensionPixelSize(
                    R.styleable.CustomSnackbar_snackbarPaddingStart,
                    paddingStart
                )
                paddingEnd = a.getDimensionPixelSize(
                    R.styleable.CustomSnackbar_snackbarPaddingEnd,
                    paddingEnd
                )
                paddingTop = a.getDimensionPixelSize(
                    R.styleable.CustomSnackbar_snackbarPaddingTop,
                    paddingTop
                )
                paddingBottom = a.getDimensionPixelSize(
                    R.styleable.CustomSnackbar_snackbarPaddingBottom,
                    paddingBottom
                )

                marginStart = a.getDimensionPixelSize(
                    R.styleable.CustomSnackbar_snackbarMarginStart,
                    marginStart
                )
                marginEnd =
                    a.getDimensionPixelSize(R.styleable.CustomSnackbar_snackbarMarginEnd, marginEnd)
                marginTop =
                    a.getDimensionPixelSize(R.styleable.CustomSnackbar_snackbarMarginTop, marginTop)
                marginBottom = a.getDimensionPixelSize(
                    R.styleable.CustomSnackbar_snackbarMarginBottom,
                    marginBottom
                )

                duration = when (a.getInt(R.styleable.CustomSnackbar_snackbarDuration, 0)) {
                    0 -> Snackbar.LENGTH_SHORT
                    1 -> Snackbar.LENGTH_LONG
                    else -> Snackbar.LENGTH_INDEFINITE
                }
            }

        // AUTO-SHOW when view is attached
        addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: android.view.View) {
                if (autoShow) Handler(Looper.getMainLooper()).post { show() }
            }

            override fun onViewDetachedFromWindow(v: android.view.View) {}
        })
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val density = resources.displayMetrics.density

        // Calculate text dimensions
        textPaint.color = textColor
        val textHeight = textPaint.descent() - textPaint.ascent()

        // Calculate icon contribution
        val iconHeight = if (icon != null) iconSize * density else 0f

        // Content height is the maximum of text height and icon height
        val contentHeight = maxOf(textHeight, iconHeight)

        // Total height including padding
        val totalHeight = (paddingTop * density + contentHeight + paddingBottom * density).toInt()

        // For width, if it's match_parent, use parent's width, otherwise wrap content
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val finalWidth = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> widthSize
            else -> widthSize // UNSPECIFIED - use available width
        }

        setMeasuredDimension(
            finalWidth,
            resolveSize(totalHeight, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val density = resources.displayMetrics.density

        // Draw background with rounded corners (preview)
        paint.color = backgroundColor
        paint.style = Paint.Style.FILL
        rectF.set(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawRoundRect(rectF, cornerRadius * density, cornerRadius * density, paint)

        var currentX = paddingStart * density
        val centerY = height / 2f

        // Draw icon if present
        icon?.let {
            val iconSizePx = (iconSize * density).toInt()
            val iconTop = (centerY - iconSizePx / 2).toInt()
            it.setBounds(
                currentX.toInt(),
                iconTop,
                (currentX + iconSizePx).toInt(),
                iconTop + iconSizePx
            )
            it.draw(canvas)
            currentX += iconSizePx + iconPadding * density
        }

        // Draw text
        textPaint.color = textColor
        val textY = centerY - (textPaint.descent() + textPaint.ascent()) / 2
        canvas.drawText(text, currentX, textY, textPaint)
    }

    private fun findSuitableParent(): ViewGroup? {
        var parent: ViewParent? = parent
        while (parent is View) {
            if (parent is ViewGroup) {
                return parent
            }
            parent = parent.parent
        }

        // Fallback: Try to get Activity's content view
        return (context as? Activity)?.findViewById(android.R.id.content)
            ?: (context as? ContextWrapper)?.baseContext?.let { ctx ->
                (ctx as? Activity)?.findViewById(android.R.id.content)
            }
    }

    fun show() {
        val parent = targetParentView ?: findSuitableParent() ?: return

        snackbar = Snackbar.make(parent, text, duration).apply {
            val view = this.view

            // Apply padding
            view.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom)

            // Apply background + corner radius
            view.background = GradientDrawable().apply {
                setColor(backgroundColor)
                cornerRadius = this@CustomSnackbar.cornerRadius
            }
            view.elevation = 0f

            // Apply margins
            (view.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                marginStart = this@CustomSnackbar.marginStart
                marginEnd = this@CustomSnackbar.marginEnd
                topMargin = this@CustomSnackbar.marginTop
                bottomMargin = this@CustomSnackbar.marginBottom
                view.layoutParams = this
            }

            // Apply text and icon
            val textView =
                view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            textView.text = text
            textView.setTextColor(textColor)

            icon?.let {
                val size = (iconSize * resources.displayMetrics.density).toInt()
                it.setBounds(0, 0, size, size)
                textView.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
                textView.compoundDrawablePadding = iconPadding
            }
        }

        snackbar?.show()
    }

    // Programmatic API
    fun setParentView(view: View) = apply { targetParentView = view }

    fun setText(text: String) = apply {
        this.text = text
        requestLayout()
        invalidate()
    }

    fun setTextColor(color: Int) = apply {
        this.textColor = color
        invalidate()
    }

    fun setIcon(icon: Drawable?) = apply {
        this.icon = icon
        requestLayout()
        invalidate()
    }

    fun setIconSize(sizeDp: Float) = apply {
        this.iconSize = sizeDp
        requestLayout()
        invalidate()
    }

    fun setSnackbarBackgroundColor(color: Int) = apply {
        this.backgroundColor = color
        invalidate()
    }

    fun setCornerRadius(radiusDp: Float) = apply {
        this.cornerRadius = radiusDp
        invalidate()
    }

    fun setIconPadding(dp: Int) = apply {
        this.iconPadding = dp
        requestLayout()
        invalidate()
    }

    fun setCustomPaddings(start: Int, top: Int, end: Int, bottom: Int) = apply {
        paddingStart = start; paddingTop = top; paddingEnd = end; paddingBottom = bottom
        requestLayout()
        invalidate()
    }

    fun setCustomMargins(start: Int, top: Int, end: Int, bottom: Int) = apply {
        marginStart = start; marginTop = top; marginEnd = end; marginBottom = bottom
    }

    fun setDuration(duration: Int) = apply { this.duration = duration }
    fun setAutoShow(show: Boolean) = apply { this.autoShow = show }

    fun dismiss() {
        snackbar?.dismiss()
    }
}