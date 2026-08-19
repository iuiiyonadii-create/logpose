plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.uriel.logpose.features.music.module"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        languageVersion = "1.9"
        apiVersion = "1.9"
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:contracts"))
    implementation(project(":core:services"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.spotify.auth)
    implementation(project(":spotify-remote"))
    implementation(libs.androidx.security.crypto)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

    testImplementation(libs.junit)
}
