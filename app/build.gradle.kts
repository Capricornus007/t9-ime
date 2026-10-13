plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// rime 的資料檔（default.yaml、朙月拼音詞庫、essay、筆畫）以子模組放在 app/data/rime/，
// 打包前匯成一個額外的 assets 來源目錄：librime 執行期是在同一個 shared/ 底下找
// import_preset 與 import_tables，四個倉攤平才找得到。
android {
    namespace = "com.capricornus.t9ime"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.capricornus.t9ime"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.2-librime"
        ndk { abiFilters += "arm64-v8a" }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/jni/CMakeLists.txt")
            version = "3.31.6"
            // 整串 librime 依賴靜態鏈進 librime_jni.so，APK 裡只多這一個 .so。
            // ANDROID_PLATFORM 對齊 minSdk，lua 那個 ANDROID_PLATFORM>=24 的補丁因此不必打。
            args += listOf(
                "-DANDROID_STL=c++_static",
                "-DANDROID_PLATFORM=android-26",
                "-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON",
            )
        }
    }
    ndkVersion = "27.3.13750724"

    sourceSets.getByName("main").assets.srcDir(layout.buildDirectory.dir("rime-assets"))

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

val stageRimeData = tasks.register("stageRimeData", Sync::class) {
    listOf("prelude", "luna-pinyin", "stroke", "essay").forEach { dir ->
        from("data/rime/$dir") { include("**/*.yaml", "**/*.txt") }
    }
    from("src/main/rime")
    into(layout.buildDirectory.dir("rime-assets/shared"))
}

// OpenCC 的簡繁對應表不進 git（子模組裡有），打包時複製；介面那個「繁／簡」切換執行期要讀它。
val stageOpenccData = tasks.register("stageOpenccData", Sync::class) {
    from("src/main/jni/OpenCC/data") { include("config/**", "dictionary/**") }
    into(layout.buildDirectory.dir("rime-assets/shared/opencc"))
    onlyIf { file("src/main/jni/OpenCC/data").exists() }
}

tasks.matching { it.name.startsWith("merge") && it.name.contains("Assets") }
    .configureEach { dependsOn(stageRimeData, stageOpenccData) }

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
}
