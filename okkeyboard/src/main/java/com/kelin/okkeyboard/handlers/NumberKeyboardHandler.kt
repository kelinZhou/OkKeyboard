package com.kelin.okkeyboard.handlers

import android.inputmethodservice.Keyboard
import android.text.Editable
import android.text.Selection
import com.kelin.okkeyboard.*

/**
 * **描述:** 数字键盘提供者。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021/3/2 4:56 PM
 *
 * **版本:** v 1.0.0
 */
class NumberKeyboardHandler(supportDecimals: Boolean) : KeyboardHandlerImpl() {

    override val keyboardTitle: Int = if (supportDecimals) R.string.number_decimals_keyboard else R.string.number_keyboard

    override val keyboardRes: Int = R.xml.keyboard_number

    override fun onKey(keyboardView: OkKeyboardView, target: Editable, primaryCode: Int, keyCodes: IntArray?) {
        onInput(target, primaryCode)
    }

    override val especialKeyCodes: IntArray? = intArrayOf(Keyboard.KEYCODE_DELETE)

    override val disabledKeyCodes: IntArray? = if (supportDecimals) null else intArrayOf(46)
}

