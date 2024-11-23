package com.muru.gradienttextview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.cos
import kotlin.math.sin

class GradientTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    var isBold = false
    var isItalic = false
    var isGradient = false
    var isCustomColor = false
    var startColor = 0
    var endColor = 0
    var angle = 0
    var enableStroke = false
    var strokeColor = Color.WHITE
    var strokeWidth = 0f

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.GradientTextView)
        isBold = typedArray.getBoolean(R.styleable.GradientTextView_isBold, false)
        isItalic = typedArray.getBoolean(R.styleable.GradientTextView_isItalic, false)
        isGradient = typedArray.getBoolean(R.styleable.GradientTextView_isGradient,true)
        isCustomColor = typedArray.getBoolean(R.styleable.GradientTextView_isCustomColor, false)
        startColor = typedArray.getColor(R.styleable.GradientTextView_startColor, 0xffD29F2C.toInt())
        endColor = typedArray.getColor(R.styleable.GradientTextView_endColor, 0xffEBD355.toInt())
        angle = typedArray.getInt(R.styleable.GradientTextView_angle, 0)
        enableStroke = typedArray.getBoolean(R.styleable.GradientTextView_enableStroke, false)
        strokeColor = typedArray.getColor(R.styleable.GradientTextView_strokeTint, Color.WHITE)
        strokeWidth = typedArray.getDimension(R.styleable.GradientTextView_strokeSize, 0f)

        typedArray.recycle()

        setCustomTypeface(isBold,isItalic)
        if(isGradient){
            enableTextGradient(text)
        }
    }

    fun isBold(enabled: Boolean){
        isBold = enabled
        setCustomTypeface(isBold,isItalic)
        requestLayout()
    }

    fun isGradient(enabled:Boolean){
        isGradient = enabled
        if(isGradient) enableTextGradient(text)
        requestLayout()
    }
    fun TextView.setCustomTypeface(isBold: Boolean, isItalic: Boolean) {
        val style = when {
            isBold && isItalic -> Typeface.BOLD_ITALIC
            isBold -> Typeface.BOLD
            isItalic -> Typeface.ITALIC
            else -> Typeface.NORMAL
        }
        setTypeface(typeface, style)
    }
    override fun onDraw(canvas: Canvas) {
        if (enableStroke) {
            // Adjust padding to account for the stroke width
            val strokePadding = strokeWidth.toInt() / 2
            setPadding(strokePadding, strokePadding, strokePadding, strokePadding)

            val originalColor = textColors
            val strokePaint = paint

            // Draw the stroke
            strokePaint.style = Paint.Style.STROKE
            strokePaint.strokeWidth = strokeWidth
            strokePaint.color = strokeColor
            paint.shader = null
            super.onDraw(canvas)

            // Restore the paint settings and draw the text
            strokePaint.style = Paint.Style.FILL
            paint.color = originalColor.defaultColor

            if (isGradient) {
                enableTextGradient(text)
            }
        }

        // Draw the gradient text
        super.onDraw(canvas)
    }


    fun enableTextGradient(text : CharSequence?) {
        if (!text.isNullOrEmpty()) {
            val text_paint = paint
            val width = text_paint.measureText(text.toString())
            // Calculate the end points of the gradient based on the angle
            val radians = Math.toRadians(angle.toDouble())
            val xEnd = (width * cos(radians)).toFloat()
            val yEnd = (textSize * sin(radians)).toFloat()
            val textShader = LinearGradient(
                0f, 0f,
                xEnd, yEnd,
                intArrayOf(
                    startColor,
                    endColor
                ),
                null,
                Shader.TileMode.CLAMP
            )
            text_paint.shader = textShader
            setCustomTypeface(isBold,isItalic)
            invalidate()
        }
    }

}