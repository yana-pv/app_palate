import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "ru.itis.android.core.plugin.ext"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.hilt.gradle.plugin)
    implementation(libs.ksp.gradle.plugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApp") {
            id = libs.plugins.app.android.application.get().pluginId
            implementationClass = "AndroidAppConventionPlugin"
        }
        register("androidLibrary") {
            id = libs.plugins.app.android.library.get().pluginId
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidAppCompose") {
            id = libs.plugins.app.compose.get().pluginId
            implementationClass = "ComposeConventionPlugin"
        }
        register("androidHilt") {
            id = libs.plugins.app.hilt.get().pluginId
            implementationClass = "HiltConventionPlugin"
        }
        register("androidDagger") {
            id = libs.plugins.app.dagger.get().pluginId
            implementationClass = "DaggerConventionPlugin"
        }
    }
}