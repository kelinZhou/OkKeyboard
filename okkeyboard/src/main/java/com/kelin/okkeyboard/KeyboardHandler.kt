package com.kelin.okkeyboard

import android.content.Context
import android.inputmethodservice.Keyboard
import android.support.annotation.StringRes
import android.text.Editable

/**
 * **描述:** 键盘提供者。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021/3/2 7:34 PM
 *
 * **版本:** v 1.0.0
 */
interface KeyboardHandler : KeyCodesProvider {

    /**
     * 键盘的标题。
     */
    @get:StringRes
    val keyboardTitle: Int

    /**
     * 获取键盘。
     * @param receiver 键盘组件。
     * @param target 键盘录入后会改变的目标。
     */
    fun getKeyboard(receiver: OkKeyboardView, target: Editable?): KeyboardHandler

    /**
     * 获取所有的Key。
     */
    fun getKeys(context: Context): List<Keyboard.Key>

    /**
     * 当用户按下按键时调用。
     * @param keyboardView 键盘组件。
     * @param target 要改变的目标。
     * @param primaryCode 当前的键码。
     */
    fun onKeyDown(keyboardView: OkKeyboardView, target: Editable, primaryCode: Int, keyCodes: IntArray?)
}