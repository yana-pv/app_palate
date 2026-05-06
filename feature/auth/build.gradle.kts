plugins {
    alias(libs.plugins.app.android.library)
    alias(libs.plugins.app.hilt)
    alias(libs.plugins.app.compose)

}

android {
    namespace = "com.example.auth"
}

dependencies {
    implementation(project(":core:utils"))
    implementation(project(":core:domain"))
    implementation(project(":core:design"))
    implementation(project(":core:navigation"))



    implementation(libs.x.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.androidx.hilt.navigation.compose)



    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}