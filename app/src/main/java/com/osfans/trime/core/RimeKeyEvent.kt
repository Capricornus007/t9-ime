package com.osfans.trime.core

/**
 * 按鍵名（`Return`、`BackSpace`、`Control+a` 這種 rime 鍵名）與 X11 keysym 的換算，
 * 實現在 `key.cc`。建構子簽名同樣是 JNI_OnLoad 就快取掉的，欄位順序不能動。
 */
class RimeKeyEvent(
    val keycode: Int,
    val modifier: Int,
    val repr: String,
) {
    companion object {
        @JvmStatic
        external fun parse(repr: String): RimeKeyEvent

        @JvmStatic
        external fun getModifierByName(name: String): Int

        @JvmStatic
        external fun getKeycodeByName(name: String): Int
    }
}
