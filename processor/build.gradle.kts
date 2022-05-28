val ksp_version: String by project

plugins {
    kotlin("multiplatform")
}

group = "com.example"
version = "1.0-SNAPSHOT"

kotlin {
    jvm()
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":annotations"))
                implementation("com.squareup:javapoet:1.13.0")
                implementation("com.google.devtools.ksp:symbol-processing-api:$ksp_version")
            }
            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")
        }
    }
}
