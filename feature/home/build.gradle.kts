plugins {
    alias(libs.plugins.app.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.app.hilt)
}

android {
    namespace = "com.example.palate.feature.home"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(project(":core:domain"))
    implementation(project(":core:design"))
    implementation(project(":core:data"))
    implementation(project(":feature:plan"))
    implementation(project(":feature:my_recipes"))
    implementation(project(":feature:shopping_list"))
    implementation(project(":feature:profile"))

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.x.lifecycle.runtime.ktx)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.foundation)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}