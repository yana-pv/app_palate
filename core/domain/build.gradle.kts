plugins {
    alias(libs.plugins.app.android.library)
}

android {
    namespace = "com.example.domain"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(project(":core:utils"))


}