pluginManagement {
    val kotlin_version: String by settings
    val ksp_version: String by settings
    plugins {
        id("com.google.devtools.ksp") version ksp_version apply false
        kotlin("multiplatform") version kotlin_version apply false
        kotlin("plugin.serialization") version kotlin_version apply false
    }
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
rootProject.name = "ktor-native"
include(":web")
include(":processor")
include(":annotations")
