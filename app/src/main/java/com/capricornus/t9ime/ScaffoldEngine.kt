package com.capricornus.t9ime

/**
 * 暫時用的假引擎：**只為了把「按數字→出候選→上屏」這條 UI 路跑通**。
 *
 * 真正的引擎是 librime（`data/shared/t9_pinyin.schema.yaml` 已在倉裡，
 * 本機也用 rime_deployer 與探針驗過：426→好/號/高、943426→一點/字典）。
 * 接上 native librime 之前，這裡只放幾條寫死的對照，**不要當成輸入法能用**。
 */
object ScaffoldEngine {
    private val table = mapOf(
        "4" to listOf("g", "h", "i"),
        "42" to listOf("ha", "ga", "ia"),
        "426" to listOf("好", "号", "高", "搞"),
        "64" to listOf("你", "米", "迷", "泥"),
        "943426" to listOf("一点", "字典", "写好", "疑点"),
        "48626426" to listOf("湖南", "湖南好", "昏暗", "国奥"),
    )

    fun candidatesFor(code: String): List<String> {
        if (code.isEmpty()) return emptyList()
        table[code]?.let { return it }
        // 沒命中整串時退回「最後一碼」的字母，讓介面至少看得見東西
        return table[code.takeLast(1)] ?: emptyList()
    }
}
