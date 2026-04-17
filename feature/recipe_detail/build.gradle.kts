plugins {
    alias(libs.plugins.app.android.library)
    alias(libs.plugins.app.compose)
    alias(libs.plugins.app.hilt)
}

android {
    namespace = "com.example.recipe_detail"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:design"))
    implementation(project(":core:navigation"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.coil.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.compose.material.icons.extended)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
