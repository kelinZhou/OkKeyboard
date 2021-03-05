package com.kelin.okkeyboard

import android.content.Context
import android.inputmethodservice.Keyboard
import android.support.annotation.XmlRes
import android.text.Editable
import android.text.Selection

/**
 * **描述:** 键盘处理的基本实现。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021/3/3 4:21 PM
 *
 * **版本:** v 1.0.0
 */
abstract class KeyboardHandlerImpl : KeyboardHandler {

    protected var keyboard: Keyboard? = null

    override fun getKeys(context: Context): List<Keyboard.Key> {
        if (keyboard == null) {
            keyboard = Keyboard(context, keyboardRes)
        }
        return keyboard!!.keys
    }

    override fun getKeyboard(receiver: OkKeyboardView, target: Editable?): KeyboardHandler {
        if (keyboard == null) {
            keyboard = Keyboard(receiver.context, keyboardRes)
        }
        receiver.keyboard = keyboard
        return this
    }

    final override fun onKeyDown(keyboardView: OkKeyboardView, target: Editable, primaryCode: Int, keyCodes: IntArray?) {
        if (disabledKeyCodes?.contains(primaryCode) != true) {
            if (primaryCode == Keyboard.KEYCODE_DELETE) {
                onDelete(target)
            } else {
                onKey(keyboardView, target, primaryCode, keyCodes)
            }
        }
    }

    abstract fun onKey(keyboardView: OkKeyboardView, target: Editable, primaryCode: Int, keyCodes: IntArray?)

    @get:XmlRes
    abstract val keyboardRes: Int

    override val disabledKeyCodes: IntArray? = null

    protected fun onDelete(target: Editable) {
        val selectionStart = Selection.getSelectionStart(target)
        if (target.isNotEmpty()) {
            if (selectionStart > 0) {
                target.delete(selectionStart - 1, selectionStart)
            }
        }
    }

    protected fun onInput(target: Editable, primaryCode:Int) {
        val selectionStart = Selection.getSelectionStart(target)
        val selectionEnd = Selection.getSelectionEnd(target)
        target.replace(selectionStart, selectionEnd, primaryCode.toChar().toString())
        val targetSelection = selectionEnd - (selectionEnd - selectionStart - 1)
        if (selectionStart != targetSelection) {
            Selection.setSelection(target, if (targetSelection > target.length) target.length else targetSelection)
        }
    }
}