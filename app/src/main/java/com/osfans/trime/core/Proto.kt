package com.osfans.trime.core

// 這幾個 data 型別是 JNI 的對面：`app/src/main/jni/librime_jni/jni-utils.h` 在
// JNI_OnLoad 就把它們全部 FindClass 過一遍，名字、巢狀類別的 `$`、建構子簽名
// 任何一個對不上，載入 libnative-lib 時就直接 abort，而不是等到呼叫才報錯。
// 所以這裡的欄位順序＝C++ 那边 NewObject 的實參順序，改一邊必須改另一邊。

class CandidateProto(
    val text: String,
    val comment: String,
    val label: String,
)

class CommitProto(val text: String?)

class CompositionProto(
    val length: Int,
    val cursorPos: Int,
    val selStart: Int,
    val selEnd: Int,
    val preedit: String?,
    val commitTextPreview: String?,
)

class ContextProto(
    val composition: CompositionProto,
    val input: String,
    val caretPos: Int,
)

class StatusProto(
    val schemaId: String,
    val schemaName: String,
    val isDisabled: Boolean,
    val isComposing: Boolean,
    val isAsciiMode: Boolean,
    val isFullShape: Boolean,
    val isSimplified: Boolean,
    val isTraditional: Boolean,
    val isAsciiPunct: Boolean,
)

class SchemaItem(
    val schemaId: String,
    val name: String,
)

sealed class Candidates {
    class Paged(
        val hasPrevious: Boolean,
        val hasNext: Boolean,
        val isHorizontal: Boolean,
        val highlighted: Int,
        val candidates: Array<CandidateProto>,
    ) : Candidates()

    class Bulk(
        val total: Int,
        val highlighted: Int,
        val candidates: Array<CandidateProto>,
    ) : Candidates()
}

class RimeResponse(
    val commit: CommitProto,
    val composition: CompositionProto,
    val candidates: Candidates,
    val status: StatusProto,
)
