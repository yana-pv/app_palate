package ru.itis.android.core.plugin.ext

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension) {
    commonExtension.apply {
        compileSdk {
            version = release(libs.findVersion("compile-sdk").get().toString().toInt())
        }
        defaultConfig.minSdk = libs.findVersion("min-sdk").get().toString().toInt()

        compileOptions.apply {
            val javaVersion = JavaVersion.toVersion(libs.findVersion("java").get())
            sourceCompatibility = javaVersion
            targetCompatibility = javaVersion
        }
    }
}

internal fun Project.configureKotlin() {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
}