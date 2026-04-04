plugins {
    alias(libs.plugins.app.android.application)
    alias(libs.plugins.app.compose)
    //alias(libs.plugins.app.dagger)
}

android {
    namespace = "com.example.palate"
    defaultConfig {
        applicationId = "com.example.palate"
        versionCode = 1
        versionName = "1.0"

    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.x.lifecycle.runtime.ktx)
    implementation(libs.x.activity.compose)
    implementation(libs.retrofit)
}