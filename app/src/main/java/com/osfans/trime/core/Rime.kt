package com.osfans.trime.core

/**
 * librime 的 JNI 介面，直接沿用 Trime 搬過來的 `app/src/main/jni/librime_jni/`，
 * 因此類別與方法名必須跟 C++ 那邊的 `Java_com_osfans_trime_core_Rime_*` 符號一致，
 * 不能改包名也不能改方法名（改了就是 UnsatisfiedLinkError）。
 */
object Rime {

    init {
        System.loadLibrary("rime_jni")
    }

    // 啟動：兩個目錄是 rime 的 shared／user 資料夾，version 寫進 distribution_version。
    @JvmStatic
    external fun startupRime(
        sharedDir: String,
        userDir: String,
        versionName: String,
        fullCheck: Boolean,
    )

    @JvmStatic
    external fun exitRime()

    @JvmStatic
    external fun syncRimeUserData(): Boolean

    @JvmStatic
    external fun deployRimeSchemaFile(schemaFile: String): Boolean

    @JvmStatic
    external fun deployRimeConfigFile(
        fileName: String,
        versionKey: String,
    ): Boolean

    @JvmStatic
    external fun processRimeKey(keycode: Int, mask: Int): Boolean

    @JvmStatic
    external fun simulateRimeKeySequence(keySequence: String): Boolean

    @JvmStatic
    external fun commitRimeComposition(): Boolean

    @JvmStatic
    external fun clearRimeComposition()

    @JvmStatic
    external fun getRimeCommit(): CommitProto

    @JvmStatic
    external fun getRimeContext(): ContextProto

    @JvmStatic
    external fun getRimeStatus(): StatusProto

    @JvmStatic
    external fun getRimeResponse(pagingMode: Boolean): RimeResponse

    @JvmStatic
    external fun setRimeOption(option: String, value: Boolean)

    @JvmStatic
    external fun getRimeOption(option: String): Boolean

    @JvmStatic
    external fun getRimeSchemaList(): Array<SchemaItem>

    @JvmStatic
    external fun getAvailableRimeSchemaList(): Array<SchemaItem>

    @JvmStatic
    external fun getSelectedRimeSchemaList(): Array<SchemaItem>

    @JvmStatic
    external fun selectRimeSchemas(schemas: Array<String>): Boolean

    @JvmStatic
    external fun getCurrentRimeSchema(): String

    @JvmStatic
    external fun selectRimeSchema(schemaId: String): Boolean

    @JvmStatic
    external fun getRimeRawInput(): String

    @JvmStatic
    external fun getRimeCaretPos(): Int

    @JvmStatic
    external fun setRimeCaretPos(caretPos: Int)

    @JvmStatic
    external fun selectRimeCandidate(index: Int, global: Boolean): Boolean

    @JvmStatic
    external fun deleteRimeCandidate(index: Int, global: Boolean): Boolean

    @JvmStatic
    external fun changeRimeCandidatePage(backward: Boolean): Boolean

    @JvmStatic
    external fun getRimeCandidates(startIndex: Int, limit: Int): Array<CandidateProto>

    /** C++ 端的 rime 通知（部署完成、切換方案、切換 option）會回呼這裡。type：1=schema 2=option 3=deploy。 */
    @Volatile
    var messageHandler: ((type: Int, value: String) -> Unit)? = null

    @JvmStatic
    fun handleRimeMessage(type: Int, args: Array<Any?>) {
        // C++ 那邊 NewObjectArray(1) 只放一個元素，值在 index 0。
        val value = args.getOrNull(0) as? String ?: return
        messageHandler?.invoke(type, value)
    }
}
