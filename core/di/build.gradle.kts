plugins {
    alias(libs.plugins.app.android.library)
}

android {
    namespace = "com.example.di"

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)


    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:network"))

    implementation(libs.hilt)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}