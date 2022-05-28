val ktor_version: String by project

tasks.register<Copy>("replace-progect-dir") {
    val dir = "${project.rootDir}"
    file("$dir/web/src/nativeInterop/cinterop").walk()
        .filter { it.name == "libpq.def" }
        .forEach {
            it.writeText(
                it.readText().replace(
                "{project-dir}", dir
                )
            )
        }
}

plugins {
    application
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization")
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        compilations["main"].cinterops {
            tasks["replace-progect-dir"].run { println("replace-progect-dir finished ... ") }
            val libpq by creating {
                defFile(project.file("src/nativeInterop/cinterop/libpq.def"))
            }
            binaries {
                executable {
                    entryPoint = "main"
                }
            }
        }
        sourceSets {
            val nativeMain by getting {
                dependencies {
                    implementation(project(":annotations"))
                    implementation("io.ktor:ktor-server-core:$ktor_version")
                    implementation("io.ktor:ktor-server-cio:$ktor_version")
                    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
                    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
                }
            }
        }
    }
}

dependencies {
    ksp(project(":processor"))
    implementation(kotlin("stdlib-jdk8"))
}

repositories {
    mavenCentral()
}

defaultTasks("chat")