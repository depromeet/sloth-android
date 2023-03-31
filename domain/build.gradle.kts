@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `java-library`
    kotlin("jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(libs.coroutine.core)
    implementation(libs.paging.common)
    implementation(libs.javax.inject)
}