plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

kotlin {
    explicitApi()
    jvmToolchain(17)
}

application {
    mainClass.set("com.thamis.ui.missioncontrol.LaunchMissionControlKt")
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:contracts"))
    implementation(project(":lab:headless-runner"))
    implementation(project(":lab:time-machine"))
    implementation(project(":lab:performance-farm"))
    implementation(project(":lab:simulation-engine"))
    implementation(project(":lab:intelligence"))
    implementation(project(":lab:orchestrator"))
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.junit)
}
