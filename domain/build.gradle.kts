@Suppress("DSL_SCOPE_VIOLATION", "PropertyName")
plugins {
    id(libs.plugins.java.library.get().pluginId)
    id(libs.plugins.kotlin.jvm.get().pluginId)
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