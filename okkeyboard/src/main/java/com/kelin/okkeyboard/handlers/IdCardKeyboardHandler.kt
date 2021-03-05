package com.kelin.okkeyboard.handlers

import android.inputmethodservice.Keyboard
import android.text.Editable
import com.kelin.okkeyboard.KeyboardHandlerImpl
import com.kelin.okkeyboard.OkKeyboardView
import com.kelin.okkeyboard.*

/**
 * **描述:** 身份证键盘的处理者。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021/3/3 4:30 PM
 *
 * **版本:** v 1.0.0
 */
class IdCardKeyboardHandler : KeyboardHandlerImpl() {

    override val keyboardTitle: Int = R.string.id_card_keyboard

    override val keyboardRes: Int = R.xml.keyboard_id_card_zn

    override fun onKey(keyboardView: OkKeyboardView, target: Editable, primaryCode: Int, keyCodes: IntArray?) {
        if (target.length < 18) {
            onInput(target, primaryCode)
        }
    }

    override val especialKeyCodes: IntArray? = intArrayOf(Keyboard.KEYCODE_DELETE)
}