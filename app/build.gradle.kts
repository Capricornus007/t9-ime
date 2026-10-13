plugins {
    // AGP 9 內建 Kotlin 支援：再套 org.jetbrains.kotlin.android 會直接被擋下來
    // （CI 那輪的訊息就是這個），只留 compose 編譯器外掛。
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

// rime 的資料檔（default.yaml、朙月拼音詞庫、essay、筆畫）以子模組放在 app/data/rime/，
// 打包前匯成一個額外的 assets 來源目錄：librime 執行期是在同一個 shared/ 底下找
// import_preset 與 import_tables，四個倉攤平才找得到。
android {
    namespace = "com.capricornus.t9ime"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.capricornus.t9ime"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.2-librime"
        ndk { abiFilters += "arm64-v8a" }
        // 編譯旗標只能掛在 defaultConfig 這邊：android 層那個 externalNativeBuild.cmake
        // 是 Cmake 介面（只有 path／version），flags 在 CmakeFlags 介面裡，掛錯地方編不過。
        // 整串 librime 依賴靜態鏈進 librime_jni.so，APK 裡只多這一個 .so。
        // ANDROID_PLATFORM 對齊 minSdk，lua 那個 ANDROID_PLATFORM>=24 的補丁因此不必打。
        externalNativeBuild {
            cmake {
                arguments(
                    "-DANDROID_STL=c++_static",
                    "-DANDROID_PLATFORM=android-26",
                    "-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON",
                )
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/jni/CMakeLists.txt")
            version = "3.31.6"
        }
    }
    ndkVersion = "27.3.13750724"

    sourceSets.getByName("main").assets.srcDir(layout.buildDirectory.dir("rime-assets"))

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
    implementation("androidx.core:core-ktx:1.17.0")
    // BOM 2026.09.00 鎖的 material3 是 1.4.0，實查它的 classes.jar 裡
    // MaterialShapes／MotionScheme.expressive()／ShapeDefaults.ExtraExtraLarge 都還是 internal
    // （名字帶 $material3 後綴），外部叫不到。M3 Expressive 真正公開的那批在 1.5.x，
    // 所以 material3 單獨指定，其餘（ui／foundation）仍走 BOM。
    implementation(platform("androidx.compose:compose-bom:2026.09.00"))
    implementation("androidx.compose.material3:material3:1.5.0-beta01")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
