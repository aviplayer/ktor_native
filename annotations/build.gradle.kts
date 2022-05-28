plugins {
    kotlin("multiplatform")
}

group = "com.annotation"
version = "1.0-SNAPSHOT"

kotlin {
    jvm()
    linuxX64()
    macosX64()
    sourceSets {
        val commonMain by getting {
            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")
        }
    }
}
