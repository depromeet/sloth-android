@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.google.service)
    alias(libs.plugins.secrets.gradle.plugin)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kapt)
}

android {
    namespace = "com.depromeet.presentation"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField(
            "String",
            "POLICY_WEB_VIEW_URL",
            properties["POLICY_WEB_VIEW_URL"] as String
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation(project(":domain"))

    implementation(libs.core)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)

    implementation(libs.bundles.navigation)
    implementation(libs.bundles.lifecycle)
    implementation(libs.activity)
    implementation(libs.fragment)
    implementation(libs.recyclerview)
    implementation(libs.paging.runtime)

    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.fragment)
    kapt(libs.hilt.compiler)

    implementation(libs.bundles.coroutine)

    implementation(libs.google.auth)
    implementation(libs.kakao.auth)

    implementation(libs.progressview)
    implementation(libs.lottie)
    implementation(libs.timber)
    implementation(libs.glide)
    kapt(libs.glide.compiler)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)

    implementation(libs.splash)

    implementation(libs.calendar.view)
    implementation(libs.photoview)
    implementation(libs.android.image.cropper)
}