package com.kelin.okkeyboard

/**
 * **描述:** 键码提供者
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021/3/2 4:10 PM
 *
 * **版本:** v 1.0.0
 */
interface KeyCodesProvider {
    /**
     * 特俗的键码。
     */
    val especialKeyCodes: IntArray?

    /**
     * 要禁用的键码。
     */
    val disabledKeyCodes: IntArray?
}