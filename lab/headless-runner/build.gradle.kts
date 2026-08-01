plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    explicitApi()
    jvmToolchain(17)
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:contracts"))
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.junit)
}
