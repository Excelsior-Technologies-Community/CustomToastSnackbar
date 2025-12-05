package com.ext.custom_toast_snackbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.use

class CustomToast @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var text: String = "Custom Toast"
    private var textColor: Int = Color.WHITE
    private var icon: Drawable? = null
    private var iconSize: Float = 28f
    private var backgroundColor: Int = Color.parseColor("#323232")
    private var cornerRadius: Float = 16f
    private var iconPadding: Int = 12
    private var paddingStart = 10
    private var paddingTop = 10
    private var paddingEnd = 10
    private var paddingBottom = 10
    private var marginStart = 0
    private var marginTop = 0
    private var marginEnd = 0
    private var marginBottom = 40
    private var duration = Toast.LENGTH_SHORT
    private var autoShow = true

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 14f * resources.displayMetrics.scaledDensity
        isFakeBoldText = true
    }
    private val rectF = RectF()

    init {
        visibility = VISIBLE
        setWillNotDraw(false) // Enable drawing

        context.obtainStyledAttributes(attrs, R.styleable.CustomToast).use { a ->
            text = a.getString(R.styleable.CustomToast_toastText) ?: "Custom Toast"
            textColor = a.getColor(R.styleable.CustomToast_toastTextColor, textColor)
            icon = a.getDrawable(R.styleable.CustomToast_toastIcon)
            iconSize = a.getDimension(R.styleable.CustomToast_toastIconSize, iconSize)
            backgroundColor =
                a.getColor(R.styleable.CustomToast_toastBackgroundColor, backgroundColor)
            cornerRadius = a.getDimension(R.styleable.CustomToast_toastCornerRadius, cornerRadius)
            iconPadding =
                a.getDimensionPixelSize(R.styleable.CustomToast_toastIconPadding, iconPadding)
            paddingStart =
                a.getDimensionPixelSize(R.styleable.CustomToast_toastPaddingStart, paddingStart)
            paddingEnd =
                a.getDimensionPixelSize(R.styleable.CustomToast_toastPaddingEnd, paddingEnd)
            paddingTop =
                a.getDimensionPixelSize(R.styleable.CustomToast_toastPaddingTop, paddingTop)
            paddingBottom =
                a.getDimensionPixelSize(R.styleable.CustomToast_toastPaddingBottom, paddingBottom)
            marginStart =
                a.getDimensionPixelSize(R.styleable.CustomToast_toastMarginStart, marginStart)
            marginEnd = a.getDimensionPixelSize(R.styleable.CustomToast_toastMarginEnd, marginEnd)
            marginTop = a.getDimensionPixelSize(R.styleable.CustomToast_toastMarginTop, marginTop)
            marginBottom =
                a.getDimensionPixelSize(R.styleable.CustomToast_toastMarginBottom, marginBottom)
            duration = when (a.getInt(R.styleable.CustomToast_toastDuration, 0)) {
                0 -> Toast.LENGTH_SHORT
                1 -> Toast.LENGTH_LONG
                else -> Toast.LENGTH_SHORT
            }
        }

        // AUTO-SHOW when attached
        addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: android.view.View) {
                if (autoShow) {
                    Handler(Looper.getMainLooper()).post { show() }
                }
            }

            override fun onViewDetachedFromWindow(v: android.view.View) {}
        })
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val density = resources.displayMetrics.density

        // Calculate text dimensions
        textPaint.color = textColor
        val textWidth = textPaint.measureText(text)
        val textHeight = textPaint.descent() - textPaint.ascent()

        // Calculate icon contribution
        val iconWidth = if (icon != null) (iconSize * density + iconPadding * density) else 0f
        val iconHeight = if (icon != null) iconSize * density else 0f

        // Content height is the maximum of text height and icon height
        val contentHeight = maxOf(textHeight, iconHeight)

        // Total dimensions including padding
        val totalWidth =
            (paddingStart * density + iconWidth + textWidth + paddingEnd * density).toInt()
        val totalHeight = (paddingTop * density + contentHeight + paddingBottom * density).toInt()

        setMeasuredDimension(
            resolveSize(totalWidth, widthMeasureSpec),
            resolveSize(totalHeight, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val density = resources.displayMetrics.density

        // Draw background with rounded corners
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

    fun show() {
        val density = resources.displayMetrics.density
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(
                (paddingStart * density).toInt(),
                (paddingTop * density).toInt(),
                (paddingEnd * density).toInt(),
                (paddingBottom * density).toInt()
            )
            background = GradientDrawable().apply {
                setColor(backgroundColor)
                cornerRadius = this@CustomToast.cornerRadius * density
            }
        }

        icon?.let {
            ImageView(context).apply {
                setImageDrawable(it)
                val size = (iconSize * density).toInt()
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    marginEnd = (iconPadding * density).toInt()
                }
            }.also { layout.addView(it) }
        }

        TextView(context).apply {
            this.text = this@CustomToast.text
            setTextColor(textColor)
            textSize = 14f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }.also { layout.addView(it) }

        Toast(context.applicationContext).apply {
            this.duration = this@CustomToast.duration
            view = layout
            val xOffset = ((marginStart - marginEnd) * density).toInt()
            val yOffset = ((marginTop - marginBottom) * density).toInt()
            setGravity(Gravity.CENTER, xOffset, yOffset)
            show()
        }
    }

    // Setters
    fun setToastBackgroundColor(color: Int) = apply {
        backgroundColor = color
        invalidate()
    }

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

    fun setCornerRadius(radiusDp: Float) = apply {
        this.cornerRadius = radiusDp
        invalidate()
    }

    fun setDuration(duration: Int) = apply { this.duration = duration }
    fun setCustomMargins(start: Int, top: Int, end: Int, bottom: Int) = apply {
        marginStart = start; marginTop = top; marginEnd = end; marginBottom = bottom
    }

    fun setAutoShow(show: Boolean) = apply { this.autoShow = show }
}