plugins {
    alias(libs.plugins.app.android.library)
    alias(libs.plugins.app.compose)
}

android {
    namespace = "com.example.main"
}

dependencies {
    implementation(project(":core:navigation"))
    implementation(project(":core:design"))

    implementation(project(":feature:home"))
    implementation(project(":feature:plan"))
    implementation(project(":feature:my_recipes"))
    implementation(project(":feature:shopping_list"))
    implementation(project(":feature:profile"))

    implementation(libs.compose.navigation)
    implementation(libs.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.core.ktx)
}
