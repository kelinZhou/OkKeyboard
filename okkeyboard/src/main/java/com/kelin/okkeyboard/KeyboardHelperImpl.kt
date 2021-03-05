package com.kelin.okkeyboard

import android.annotation.SuppressLint
import android.inputmethodservice.KeyboardView
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewTreeObserver
import android.view.accessibility.AccessibilityEvent
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText


/**
 * **描述:** 用于处理自定义键盘的帮助者。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021/2/24 4:50 PM
 *
 * **版本:** v 1.0.0
 */
class KeyboardHelperImpl internal constructor(
    private val target: EditText,
    private val keyboardView: OkKeyboardView,
    private val keyboardContainer: View,
    private var keyboardHandler: KeyboardHandler
) : KeyboardHelper {

    /**
     * 显示键盘时的动画。
     */
    private val animShow by lazy {
        TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 1.0f,
            Animation.RELATIVE_TO_SELF, 0.0f
        ).apply {
            setAnimationListener(AnimListener {
                keyboardContainer.clearAnimation()
            })
            duration = 300
        }
    }

    private val hiddenAnimListener by lazy { AnimListener {
        if (it) {
            keyboardContainer.visibility = View.GONE
        }
    } }

    /**
     * 隐藏键盘时的动画。
     */
    private val animHidden by lazy {
        TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 1.0f
        ).apply {
            setAnimationListener(hiddenAnimListener)
            duration = 300
        }
    }

    /**
     * 选中改变监听。
     */
    private val focusChangeListener by lazy { GlobalFocusChangeListener() }

    override val isShowing: Boolean
        get() = keyboardContainer.visibility == View.VISIBLE

    private var hideSoftInput = false

    private val runnableShow = Runnable { showKeyboard() }

    init {
        keyboardView.apply {
            isEnabled = true
            isPreviewEnabled = false
            setOnKeyboardActionListener(CarKeyboardActionListener())
        }
        listenerTargetCursorChanged()

        target.rootView.viewTreeObserver.addOnGlobalFocusChangeListener(focusChangeListener)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun listenerTargetCursorChanged() {
        target.setAccessibilityDelegate(object : View.AccessibilityDelegate() {
            override fun sendAccessibilityEvent(host: View?, eventType: Int) {
                super.sendAccessibilityEvent(host, eventType)
                if (eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED || eventType == AccessibilityEvent.TYPE_VIEW_CLICKED || eventType == AccessibilityEvent.TYPE_VIEW_FOCUSED) {
                    keyboardHandler = keyboardHandler.getKeyboard(keyboardView, target.text)
                    keyboardView.keyCodesProvider = keyboardHandler
                    if (eventType == AccessibilityEvent.TYPE_VIEW_CLICKED || eventType == AccessibilityEvent.TYPE_VIEW_FOCUSED) {
                        if (hideSoftInput) {
                            hideSoftInput = false
                            target.postDelayed(runnableShow, 200)
                        } else {
                            showKeyboard()
                        }
                    }
                }
            }
        })
    }

    override fun showKeyboard() {
        keyboardContainer.apply {
            if (visibility != View.VISIBLE) {
                visibility = View.VISIBLE
                startAnimation(animShow)
            }
        }
    }

    override fun hiddenKeyboard() {
        keyboardContainer.apply {
            if (visibility != View.GONE && !hiddenAnimListener.isRunning) {
                startAnimation(animHidden)
            }
        }
    }

    override fun switchKeyboard(type: KeyboardHandler) {
        keyboardHandler = type.getKeyboard(keyboardView, null)
        keyboardView.keyCodesProvider = keyboardHandler
    }

    private inner class CarKeyboardActionListener : KeyboardView.OnKeyboardActionListener {
        override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
            keyboardHandler.onKeyDown(keyboardView, target.text, primaryCode, keyCodes)
        }

        override fun onPress(primaryCode: Int) {
        }

        override fun onRelease(primaryCode: Int) {
        }

        override fun onText(text: CharSequence?) {
        }

        override fun swipeLeft() {
        }

        override fun swipeRight() {
        }

        override fun swipeDown() {
        }

        override fun swipeUp() {
        }
    }

    private inner class GlobalFocusChangeListener : ViewTreeObserver.OnGlobalFocusChangeListener {
        override fun onGlobalFocusChanged(oldFocus: View?, newFocus: View?) {
            if (oldFocus == target) {
                if (newFocus == target) {
                    showKeyboard()
                } else {
                    hiddenKeyboard()
                }
            } else {
                if (oldFocus != null) {
                    ContextCompat.getSystemService(target.context, InputMethodManager::class.java)?.run {
                        hideSoftInput = hideSoftInputFromWindow(oldFocus.windowToken, 0)
                    }
                }
            }
        }
    }

    private class AnimListener(private val onEnd: (isStart: Boolean) -> Unit) : Animation.AnimationListener {
        var isRunning: Boolean = false

        override fun onAnimationStart(animation: Animation?) {
            isRunning = true
        }

        override fun onAnimationEnd(animation: Animation?) {
            onEnd(isRunning)
            isRunning = false
        }

        override fun onAnimationRepeat(animation: Animation?) {
        }
    }
}