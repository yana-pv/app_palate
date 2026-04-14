plugins {
    alias(libs.plugins.app.android.library)
}

android {
    namespace = "com.example.palate.core.navigation"
}

dependencies {
    implementation(libs.androidx.core.ktx)
}