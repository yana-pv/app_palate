plugins {
    alias(libs.plugins.app.android.library)
    alias(libs.plugins.app.hilt)
}

android {
    namespace = "com.example.translation"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:utils"))
    
    implementation("com.google.mlkit:translate:17.0.3")
    implementation(libs.kotlinx.coroutines.play.services)

    implementation(libs.androidx.core.ktx)
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)
}
