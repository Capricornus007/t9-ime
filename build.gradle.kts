plugins {
    // 版本對齊 nashira（同一台機器、同一條 CI 已經跑綠過的組合）：
    // AGP 9.4.1 要 Gradle 9.x，Kotlin 2.4.20 配 compose 編譯器外掛。
    id("com.android.application") version "9.4.1" apply false
    id("org.jetbrains.kotlin.android") version "2.4.20" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.20" apply false
}
