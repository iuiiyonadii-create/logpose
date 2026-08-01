pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "LogPose"
include(":app")
include(":core:common")
include(":core:contracts")
include(":lab:headless-runner")
include(":lab:time-machine")
include(":lab:performance-farm")
include(":lab:simulation-engine")
include(":lab:intelligence")
include(":lab:orchestrator")
include(":lab:evidence")
include(":ui:mission-control")