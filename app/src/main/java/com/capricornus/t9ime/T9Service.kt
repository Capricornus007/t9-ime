package com.capricornus.t9ime

import android.inputmethodservice.InputMethodService
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView

/**
 * 九鍵第一版（真引擎）：3×4 數字鍵＋上方候選列，消歧交給 librime。
 *
 * 按鍵只送數字 keysym（2=abc 3=def … 9=wxyz，跟 gboard／搜狗一致，不做多擊循環），
 * 方案 `t9_pinyin` 的 speller/algebra 把拼音整體轉寫成數字碼，候選順序就是詞庫詞頻。
 * 左側「已按碼欄」與兩套功能鍵佈局（可切換）是接下來的第一件事，
 * 規格與參考實作在 `third_party/fcitx5-t9/`（T9SideRailUi）與
 * `third_party/trime-9key-config/trime.yaml`（35 處 t9_pick_* 鍵位）。
 */
class T9Service : InputMethodService() {

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

    override fun onStartInput(attribute: android.view.inputmethod.EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        RimeEngine.stageAssets(this)
        RimeEngine.startup(this)
        RimeEngine.selectSchema("t9_pinyin")
        RimeEngine.onDeployed = {
            // 詞庫編完才選得到方案，選成功要回頭把候選列刷一次。
            runOnUiThread {
                RimeEngine.selectSchema("t9_pinyin")
                refresh()
            }
        }
        refresh()
    }

    private fun onKey(digit: String) {
        if (!RimeEngine.ready) return
        val consumed = when (digit) {
            "⌫" -> RimeEngine.sendKey(RimeEngine.KEY_BACKSPACE)
            "⏎" -> {
                // 有編碼時 Enter 交給 rime（可能是上屏候選），空欄時才是送出換行。
                if (RimeEngine.getOption("ascii_mode")) false else RimeEngine.sendKey(RimeEngine.KEY_RETURN)
            }
            else -> if (digit.isNotEmpty() && digit[0].isDigit()) {
                // 數字字元本身的碼位就是它的 X11 keysym（'2' = 0x32）。
                RimeEngine.sendKey(digit[0].code)
            } else false
        }
        drainCommit()
        refresh()
        if (!consumed && digit == "⏎") {
            val now = android.os.SystemClock.uptimeMillis()
            val down = android.view.KeyEvent(now, now, android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_ENTER, 0)
            val up = android.view.KeyEvent(now, now, android.view.KeyEvent.ACTION_UP, android.view.KeyEvent.KEYCODE_ENTER, 0)
            currentInputConnection?.sendKeyEvent(down)
            currentInputConnection?.sendKeyEvent(up)
        }
    }

    /** rime 決定要上屏的東西（選字、換頁後直接成詞、標點上屏）都走這裡，不這樣讀會漏字。 */
    private fun drainCommit() {
        val text = RimeEngine.commitText() ?: return
        currentInputConnection?.commitText(text, 1)
    }

    private fun refresh() {
        if (!RimeEngine.ready) return
        preedit.text = RimeEngine.preedit()
        candidateRow.removeAllViews()
        for ((index, candidate) in RimeEngine.candidates().withIndex()) {
            val b = Button(this).apply {
                text = candidate.text
                isAllCaps = false
                textSize = 18f
                setOnClickListener {
                    if (RimeEngine.selectCandidate(index)) {
                        drainCommit()
                        refresh()
                    }
                }
            }
            candidateRow.addView(
                b,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                ),
            )
        }
    }
}
