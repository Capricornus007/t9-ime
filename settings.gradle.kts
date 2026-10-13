pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // 本機到 dl.google.com 常在凌晨被斷線（握手成功後無字節），設了這個環境變數就改走阿里鏡像。
        // CI 上不設，維持原本的 google()，不拖慢 GitHub runner。
        if (System.getenv("T9_CN_MIRROR") != null) {
            maven(url = "https://maven.aliyun.com/repository/google")
            maven(url = "https://maven.aliyun.com/repository/central")
        }
        google()
        mavenCentral()
    }
}
rootProject.name = "t9-ime"
include(":app")
