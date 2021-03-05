package com.kelin.okkeyboard

import android.app.Activity
import android.os.Build
import android.text.InputType
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat


/**
 * **描述:** 描述如何帮助处理自定义键盘。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021/2/25 1:41 PM
 *
 * **版本:** v 1.0.0
 */
interface KeyboardHelper {

    /**
     * 键盘是否正在显示。
     */
    val isShowing: Boolean

    /**
     * 显示键盘。
     */
    fun showKeyboard()

    /**
     * 隐藏键盘。
     */
    fun hiddenKeyboard()

    /**
     * 切换到指定键盘。
     * @param type 键盘提供者。
     */
    fun switchKeyboard(type: KeyboardHandler)

    companion object {
        fun init(activity: Activity, target: EditText, factory: Factory): KeyboardHelper {
            ContextCompat.getSystemService(activity, InputMethodManager::class.java)?.run {
                val isOpen = isActive
                if (isOpen) {
                    hideSoftInputFromWindow(target.windowToken, 0)
                }
            }

            val currentVersion = Build.VERSION.SDK_INT
            var methodName: String? = null
            if (currentVersion >= 16) {
                methodName = "setShowSoftInputOnFocus"
            } else if (currentVersion >= 14) {
                methodName = "setSoftInputShownOnFocus"
            }

            if (methodName == null) {
                target.inputType = InputType.TYPE_NULL
            } else {
                try {
                    val setShowSoftInputOnFocus = EditText::class.java.getMethod(methodName, Boolean::class.java)
                    setShowSoftInputOnFocus.isAccessible = true
                    setShowSoftInputOnFocus.invoke(target, false)
                } catch (e: NoSuchMethodException) {
                    target.inputType = InputType.TYPE_NULL
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return factory.create()
        }
    }

    interface Factory {
        fun create(): KeyboardHelper
    }
}