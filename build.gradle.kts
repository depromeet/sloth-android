// Top-level build file where you can add configuration options common to all sub-projects/module
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.navigation.safeargs) apply false
    alias(libs.plugins.secrets.gradle.plugin) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.google.service) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}