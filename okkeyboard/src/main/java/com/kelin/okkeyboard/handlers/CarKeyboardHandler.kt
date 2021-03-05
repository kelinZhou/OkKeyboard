package com.kelin.okkeyboard.handlers

import android.inputmethodservice.Keyboard
import android.text.Editable
import android.text.Selection
import com.kelin.okkeyboard.*

/**
 * **描述:** 车牌号键盘提供者。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021/3/2 4:56 PM
 *
 * **版本:** v 1.0.0
 */
sealed class CarKeyboardHandler : KeyboardHandlerImpl() {

    override val keyboardTitle: Int = R.string.license_plate_keyboard

    override fun getKeyboard(receiver: OkKeyboardView, target: Editable?): KeyboardHandler {
        if (keyboard == null) {
            keyboard = Keyboard(receiver.context, keyboardRes)
        }
        return if (target == null) {
            if (receiver.keyboard != keyboard) {
                receiver.keyboard = keyboard
            }
            this
        } else {
            when (Selection.getSelectionStart(target)) {
                0 -> {
                    Capital
                }
                1 -> {
                    Registry
                }
                else -> {
                    CarNumber
                }
            }.getKeyboard(receiver, null)
        }
    }


    override fun onKey(keyboardView: OkKeyboardView, target: Editable, primaryCode: Int, keyCodes: IntArray?) {
        val selectionStart = Selection.getSelectionStart(target)
        when (primaryCode) {
            Keyboard.KEYCODE_MODE_CHANGE -> if (this is Capital) {
                if (selectionStart == 1) {
                    Registry
                } else {
                    CarNumber
                }
            } else {
                Capital
            }.also {
                if (keyboardView.keyboard != it.keyboard) {
                    keyboardView.keyboard = it.keyboard
                }
            }
            else -> {
                if (target.length < 8 || selectionStart < target.length) {
                    val capitalsLabel = Capital.getKeys(keyboardView.context).map { it.label }
                    if (selectionStart == 0 && capitalsLabel.contains(target.firstOrNull()?.toString() ?: " ")) {
                        target.replace(0, 1, "")
                    }
                    onInput(target, primaryCode)
                }
            }
        }
    }

    override val especialKeyCodes: IntArray? = intArrayOf(Keyboard.KEYCODE_MODE_CHANGE, Keyboard.KEYCODE_DELETE)


    object Capital : CarKeyboardHandler() {

        override val keyboardRes: Int = R.xml.keyboard_car_capital
    }

    object Registry : CarKeyboardHandler() {

        override val keyboardRes: Int = R.xml.keyboard_car_number

        //override val disabledKeyCodes: IntArray? = intArrayOf(48, 49, 50, 51, 52, 53, 54, 55, 56, 57)
    }

    object CarNumber : CarKeyboardHandler() {

        override val keyboardRes: Int = R.xml.keyboard_car_number
    }
}

