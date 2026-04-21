plugins {
    alias(libs.plugins.app.android.application)
    alias(libs.plugins.app.compose)
    alias(libs.plugins.app.dagger)
    alias(libs.plugins.app.hilt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
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
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.x.activity.compose)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.retrofit)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)

    implementation(project(":core:design"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:network"))
    implementation(project(":core:navigation"))
    implementation(project(":core:utils"))
    
    implementation(project(":feature:home"))
    implementation(project(":feature:recipe_detail"))
    implementation(project(":feature:plan"))
    implementation(project(":feature:my_recipes"))
    implementation(project(":feature:shopping_list"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:main"))


    implementation(libs.hilt)
    ksp(libs.hilt.compiler)

    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.x.activity.compose)
    implementation(libs.androidx.navigation.compose)
}
