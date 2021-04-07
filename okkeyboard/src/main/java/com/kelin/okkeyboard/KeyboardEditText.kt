package com.kelin.okkeyboard

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.widget.*
import androidx.annotation.IdRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.ScrollingView
import com.kelin.okkeyboard.handlers.CarKeyboardHandler
import com.kelin.okkeyboard.handlers.IdCardKeyboardHandler
import com.kelin.okkeyboard.handlers.NumberKeyboardHandler
import kotlinx.android.synthetic.main.keyboard_layout_car_number.view.*


/**
 * **描述:** 自动使用自定义键盘的文本编辑框。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021/2/24 4:28 PM
 *
 * **版本:** v 1.0.0
 */
class KeyboardEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) : AppCompatEditText(context, attrs, defStyleAttr), KeyboardHelper {

    companion object {
        /**
         * 没有指定编辑类型。
         */
        const val EDIT_TYPE_NULL = -1

        /**
         * 编辑类型：车牌号。
         */
        const val EDIT_TYPE_LICENSE_PLATE = 0x01

        /**
         * 编辑类型：纯数字。
         */
        const val EDIT_TYPE_NUMBER = 0x02

        /**
         * 编辑类型：小数类型的数字。
         */
        const val EDIT_TYPE_NUMBER_DECIMALS = 0x03

        /**
         * 编辑类型：身份证号。
         */
        const val EDIT_TYPE_ID_CARD = 0x04
    }

    @IdRes
    private val rootViewId: Int

    private val editType: Int

    private var helper: KeyboardHelper? = null

    init {
        context.obtainStyledAttributes(attrs, R.styleable.KeyboardEditText).run {
            rootViewId = getResourceId(R.styleable.KeyboardEditText_rootView, View.NO_ID)
            editType = getInt(R.styleable.KeyboardEditText_editType, EDIT_TYPE_NULL)
            isFocusableInTouchMode = if (editType == EDIT_TYPE_NULL) {
                true
            } else {
                getBoolean(R.styleable.KeyboardEditText_android_focusableInTouchMode, true)
            }
            recycle()
        }
    }

    private val keyboardContainer by lazy {
        val root = ((context as Activity).findViewById(rootViewId) ?: rootView as? ViewGroup ?: context.window.decorView as ViewGroup).let {
            if (it is ScrollView || it is AbsListView || it is ScrollingView) {
                it.parent as ViewGroup
            } else {
                it
            }
        }
        LayoutInflater.from(context).inflate(
            R.layout.keyboard_layout_car_number,
            root,
            false
        ).also {
            when (root) {
                is FrameLayout -> {
                    root.addView(it, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { gravity = Gravity.BOTTOM })
                }
                is RelativeLayout -> {
                    root.addView(it, RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                        addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
                    })
                }
                is LinearLayout -> {
                    root.addView(it)
                }
                else -> {
                    root.addView(it)
                }
            }
            it.visibility = View.GONE
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        post {
            if (editType != EDIT_TYPE_NULL) {
                helper = KeyboardHelper.init(context as Activity, this, onCreateHelperFactory())
                keyboardContainer.kelinIvKeyboardFolder.setOnClickListener { hiddenKeyboard() }
            }
        }
    }

    private fun onCreateHelperFactory(): KeyboardHelper.Factory {
        return object : KeyboardHelper.Factory {
            override fun create(): KeyboardHelper {
                return getKeyboardHandlerByEditType(editType).let { handler ->
                    keyboardContainer.kelinTvKeyboardTitle.setText(handler.keyboardTitle)
                    KeyboardHelperImpl(this@KeyboardEditText, keyboardContainer.kelinKeyboard, keyboardContainer, handler)
                }
            }
        }
    }

    private fun getKeyboardHandlerByEditType(editType: Int): KeyboardHandler {
        return when (editType) {
            EDIT_TYPE_LICENSE_PLATE -> CarKeyboardHandler.Capital
            EDIT_TYPE_NUMBER -> NumberKeyboardHandler(false)
            EDIT_TYPE_NUMBER_DECIMALS -> NumberKeyboardHandler(true)
            EDIT_TYPE_ID_CARD -> IdCardKeyboardHandler()
            else -> throw RuntimeException("Not support this type yet. type: $editType")
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (editType != EDIT_TYPE_NULL && keyCode == KeyEvent.KEYCODE_BACK) {
            if (isShowing) {
                hiddenKeyboard()
                true
            } else {
                super.onKeyDown(keyCode, event)
            }
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    override val isShowing: Boolean
        get() = helper?.isShowing ?: false

    override fun showKeyboard() {
        helper?.showKeyboard()
    }

    override fun hiddenKeyboard() {
        helper?.hiddenKeyboard()
    }

    override fun switchKeyboard(type: KeyboardHandler) {
        helper?.switchKeyboard(type)
    }
}