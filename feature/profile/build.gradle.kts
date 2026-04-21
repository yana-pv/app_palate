plugins {
    alias(libs.plugins.app.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.app.hilt)
}

android {
    namespace = "com.example.profile"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)

    implementation(libs.x.lifecycle.runtime.ktx)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(project(":core:domain"))
    implementation(project(":core:design"))
    implementation(project(":core:utils"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}