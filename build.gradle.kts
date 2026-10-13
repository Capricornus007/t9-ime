plugins {
    // 版本對齊 nashira（同一台機器、同一條 CI 已經跑綠過的組合）。
    // 注意 AGP 9 內建 Kotlin 支援，不再套 org.jetbrains.kotlin.android（套了會被擋）。
    id("com.android.application") version "9.4.1" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.20" apply false
}
