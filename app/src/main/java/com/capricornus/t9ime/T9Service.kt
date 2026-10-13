package com.capricornus.t9ime

import android.inputmethodservice.InputMethodService
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView

/**
 * 九鍵第一版：3×4 數字鍵＋上方候選列。
 *
 * 按鍵只送抽象碼 2–9（跟 gboard／搜狗一致，不做多擊循環），消歧交給引擎；
 * 現在引擎是 [ScaffoldEngine]（假資料），下一份改接 librime。
 * 左側「已按碼欄」與兩套功能鍵佈局（可切換）是接下來的第一件事，
 * 規格與參考實作在 `third_party/fcitx5-t9/`（T9SideRailUi）與
 * `third_party/trime-9key-config/trime.yaml`（35 處 t9_pick_* 鍵位）。
 */
class T9Service : InputMethodService() {

    private val code = StringBuilder()
    private lateinit var preedit: TextView
    private lateinit var candidateRow: LinearLayout

    private val keys = listOf(
        listOf("1", "2 abc", "3 def", "4 ghi"),
        listOf("5 jkl", "6 mno", "7 pqrs", "8 tuv"),
        listOf("9 wxyz", "0", "⌫", "⏎"),
    )

    override fun onCreateInputView(): View {
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        preedit = TextView(this).apply {
            textSize = 18f
            setPadding(24, 12, 24, 4)
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.START
        }
        candidateRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        root.addView(preedit)
        root.addView(HorizontalScrollView(this).apply { addView(candidateRow) })
        val grid = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        for (row in keys) {
            val line = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            for (label in row) {
                val digit = label.trim().substringBefore(' ')
                val button = Button(this).apply {
                    text = label
                    isAllCaps = false
                    textSize = 16f
                    setOnClickListener { onKey(digit) }
                }
                line.addView(button, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
            }
            grid.addView(line)
        }
        root.addView(grid)
        return root
    }

    private fun onKey(digit: String) {
        when (digit) {
            "⌫" -> { if (code.isNotEmpty()) code.deleteCharAt(code.length - 1) }
            "⏎" -> { currentInputConnection?.sendEditorAction(0); clear() }
            else -> if (digit.isNotEmpty() && digit[0].isDigit()) code.append(digit)
        }
        refresh()
    }

    private fun refresh() {
        preedit.text = code
        candidateRow.removeAllViews()
        for (word in ScaffoldEngine.candidatesFor(code.toString()).take(9)) {
            val b = Button(this).apply {
                text = word
                isAllCaps = false
                textSize = 18f
                setOnClickListener { commit(word) }
            }
            candidateRow.addView(b, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))
        }
    }

    private fun commit(word: String) {
        currentInputConnection?.commitText(word, 1)
        clear()
        refresh()
    }

    private fun clear() {
        code.setLength(0)
        candidateRow.gravity = Gravity.NO_GRAVITY
    }
}
