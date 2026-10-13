plugins {
    // 一律取目前公開發布裡最新的那個：AGP 沒有 10，尖端是 9.5.0-alpha09（9.4.1 是最新穩定版）。
    // 注意 AGP 9 內建 Kotlin 支援，再套 org.jetbrains.kotlin.android 會被擋下來。
    id("com.android.application") version "9.5.0-alpha09" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.21" apply false
}
