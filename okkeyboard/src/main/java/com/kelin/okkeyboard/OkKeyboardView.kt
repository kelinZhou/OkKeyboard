package com.kelin.okkeyboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.util.AttributeSet
import android.util.TypedValue
import androidx.core.content.ContextCompat


/**
 * **描述:** 自定义的软键盘。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021/2/25 4:38 PM
 *
 * **版本:** v 1.0.0
 */
class OkKeyboardView @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) : KeyboardView(context, attrs, defStyleAttr) {

    private val defaultTextSize: Float by lazy { TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18F, resources.displayMetrics) }

    private val paint by lazy {
        Paint().apply {
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT
            isAntiAlias = true
            textSize = defaultTextSize
        }
    }

    /**
     * 用来记录当前的键盘类型。
     */
    var keyCodesProvider: KeyCodesProvider? = null
        set(value) {
            field = value
            invalidate()
        }

    init {
        setBackgroundColor(ContextCompat.getColor(context, R.color.color_keyboard_bg))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (keyboard != null) {
            keyboard.keys?.forEach { onDrawKey(canvas, it) }
        }
    }

    private fun onDrawKey(canvas: Canvas, key: Keyboard.Key) {
        val keyCode = key.codes[0]
        val rect = Rect(key.x, key.y, key.x + key.width, key.y + key.height)
        when {
            keyCodesProvider?.disabledKeyCodes?.contains(keyCode) == true -> { //禁用部分按键
                ContextCompat.getDrawable(context, R.drawable.bg_keyboard_key_disable)?.run {
                    bounds = rect
                    draw(canvas)
                }
                paint.color = ContextCompat.getColor(context, R.color.color_key_text_disabled)
            }
            keyCodesProvider?.especialKeyCodes?.contains(keyCode) == true -> {  //处理特殊的按键
                ContextCompat.getDrawable(context, R.drawable.bg_keyboard_key_special)?.run {
                    state = key.currentDrawableState
                    bounds = rect
                    draw(canvas)
                }
                paint.color = ContextCompat.getColor(context, R.color.color_key_text_special)
            }
            else -> {
                paint.color = ContextCompat.getColor(context, R.color.color_key_text)
            }
        }

        //绘制Key。
        if (key.icon != null) { //如果有icon就绘制icon。
            key.icon.run {
                val left = rect.left + (rect.width() - bounds.width()) / 2
                val top = rect.top + (rect.height() - bounds.height()) / 2
                bounds = Rect(left, top, left + bounds.width(), top + bounds.height())
                draw(canvas)
            }
        } else if (key.label != null) {  //没有icon时就尝试绘制label，如果没有label就不绘制了。
            paint.fontMetricsInt.run {
                canvas.drawText(key.label.toString(), rect.centerX().toFloat(), (rect.bottom + rect.top - bottom - top) / 2F, paint)
            }
        }
    }
}